package com.cherry.lucene;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;

public class IndexSearchTest {


    @Test
    public void testIndexSearch() throws Exception {
        //创建分词器（必须和创建索引时的分词器一致）
        Analyzer analyzer = new StandardAnalyzer();

        //创建查询对象，指定查询的默认域和分词器
        QueryParser queryParser = new QueryParser("fileContext", analyzer);
        //查询条件为于域名：关键词，不写域名则从默认域中查询
        Query query = queryParser.parse("fileName:demo.txt");

        //指定文档库的位置
        FSDirectory directory = FSDirectory.open(new File("E:\\git\\lucene_demo\\index"));
        //创建读索引库的reader对象
        IndexReader indexReader = IndexReader.open(directory);
        //创建索引的搜索对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //执行搜索，参数为u查询语句，指定显示多少条记录
        TopDocs topDocs = indexSearcher.search(query, 10);

        System.out.println("count="+topDocs.totalHits);
        //获取结果集
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            //获取docId
            int docId = scoreDoc.doc;
            //通过唯一docID获取到文档
            Document document = indexReader.document(docId);
            System.out.println("fileName="+document.get("fileName"));
            System.out.println("fileSize="+document.get("fileSize"));
            System.out.println("==========");
        }

    }


}
