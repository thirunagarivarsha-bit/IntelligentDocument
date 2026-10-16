package com.intellidoc.search;

import com.intellidoc.dsa.*;
import com.intellidoc.model.Document;
import com.intellidoc.nlp.NLP;
import java.util.*;

public class SearchEngine {
    private final InvertedIndex inverted=new InvertedIndex();
    private final Trie trie=new Trie();
    private final Map<Integer,Document> docs=new HashMap<>();

    public void add(Document d){
        docs.put(d.id,d);
        for(String t:d.termFrequency.keySet()){inverted.add(t,d.id);trie.insert(t);}
    }
    public void clear(){docs.clear(); inverted.clear(); trie.clear();}
    public List<SearchResult> search(String query,int limit){
        List<String> q=NLP.tokenize(query);
        if(q.isEmpty())return List.of();
        Set<Integer> candidates=inverted.getAny(q);
        // Fuzzy expansion: rescue small spelling errors and related normalized terms.
        if(candidates.isEmpty()){
            Set<String> near=new HashSet<>();
            for(String term:inverted.raw().keySet())
                for(String qt:q) if(Math.min(2,Math.max(1,qt.length()/4))>=NLP.editDistance(qt,term)) near.add(term);
            candidates=inverted.getAny(near);
        }
        int N=Math.max(1,docs.size());
        List<SearchResult> out=new ArrayList<>();
        for(int id:candidates){
            Document d=docs.get(id); double score=0; int matched=0;
            for(String term:q){
                int tf=d.termFrequency.getOrDefault(term,0);
                if(tf>0)matched++;
                int df=inverted.df(term);
                if(tf>0)score += (1+Math.log(tf)) * Math.log((N+1.0)/(df+1.0));
                // title boost
                if(d.name.toLowerCase(Locale.ROOT).contains(term))score+=1.5;
            }
            double coverage=(double)matched/q.size();
            score*= (0.75+0.25*coverage);
            String reason=matched+"/"+q.size()+" query terms matched; TF-IDF ranked";
            out.add(new SearchResult(d,score,reason));
        }
        out.sort(Comparator.comparingDouble(SearchResult::score).reversed()
                .thenComparing(r->r.document().name));
        return out.subList(0,Math.min(limit,out.size()));
    }
    public List<String> suggestions(String prefix){return trie.suggest(prefix,8);}
    public InvertedIndex index(){return inverted;}
    public Collection<Document> documents(){return docs.values();}
    public int size(){return docs.size();}
}
