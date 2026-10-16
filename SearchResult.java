package com.intellidoc.search;
import com.intellidoc.model.Document;
public record SearchResult(Document document,double score,String reason) {}
