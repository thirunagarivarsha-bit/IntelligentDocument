# IntelliDoc DSA — Review 4 Build

## What this build implements
- PDF, DOC/DOCX, PPT/PPTX, XLS/XLSX, TXT and common image ingestion through Apache Tika/ImageIO.
- Streaming text extraction into `data/extracted` to reduce peak memory pressure.
- 12,000-character chunks for large documents.
- DSA-visible inverted `HashMap` index for fast keyword candidate retrieval.
- Trie autocomplete.
- TF-IDF ranking with title boosting.
- Fuzzy fallback using Levenshtein edit distance.
- Extractive frequency-based summaries.
- SHA-256 duplicate identity for future deduplication.
- Analytics: document count, repository size, tokens, chunks, vocabulary and top terms.
- Response-time measurement on every search.
- Desktop Swing UI suitable for an Eclipse demonstration.
- OCR-ready image ingestion; native OCR requires a local Tesseract installation.

## Requirements
Java 17+ and Maven 3.x.

## Eclipse
1. File -> Import -> Existing Maven Projects.
2. Select this folder.
3. Wait for Maven dependencies to download.
4. Run `com.intellidoc.app.Main` as Java Application.

## Command line
`mvn clean package`
Then:
`java -jar target/intellidoc-dsa-1.0.0.jar`

## Image OCR
The system accepts images immediately and records image metadata. For actual text extraction from scanned images, install Tesseract OCR and configure it for Tess4J. This keeps the core DSA system runnable even if native OCR is unavailable.

## Demo script
1. Add a folder containing 5–20 PDFs/DOCX/TXT/images.
2. Show the Analytics tab: repository size, chunks, vocabulary, top terms.
3. Search a natural phrase such as `document indexing data structures`.
4. Show ranked results and response time.
5. Deliberately misspell a word to demonstrate fuzzy fallback.
6. Explain: ingestion -> preprocessing -> hash index -> candidate retrieval -> TF-IDF ranking -> analytics.
