package com.cherry.lucene;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class IndexSearchTest {

    /**
     * 使用QueryParser查询
     * @throws Exception
     */
    @Test
    public void testIndexSearch() throws Exception {
        //创建分词器（必须和创建索引时的分词器一致）
        Analyzer analyzer = new StandardAnalyzer();

        //创建查询对象，指定查询的默认域和分词器
        QueryParser queryParser = new QueryParser("fileContext", analyzer);
        //查询条件为于域名：关键词，不写域名则从默认域中查询
        Query query = queryParser.parse("fileName:demo.txt");

        queryIndexStore(query);

    }

    @Test
    public void testIndexTermSearch() throws Exception{

        //创建分词器（必须和创建索引时的分词器一致）
        Analyzer analyzer = new StandardAnalyzer();

        Term term = new Term("fileName", "demo.txt");
        Query query = new TermQuery(term);
        queryIndexStore(query);

    }

    /**
     * 数值范围查询
     * @throws Exception
     */
    @Test
    public void testNumericRangeQuery() throws Exception{
        //创建分词器（必须和创建索引时的分词器一致）
        Analyzer analyzer = new StandardAnalyzer();
        //查询文件大小在1-20之间（包括1，包括20）
        Query query = NumericRangeQuery.newLongRange("fileSize", 1L, 20L, true, true);

        queryIndexStore(query);
    }

    /**
     * 多条件查询
     * BooleanClause.Occur.MUST 相当于 and
     * BooleanClause.Occur.MUST_NOT 相当于 not
     * BooleanClause.Occur.SHOULD 相当于 or
     * @throws Exception
     */
    @Test
    public void testBooleanQuery() throws Exception{
        //创建分词器（必须和创建索引时的分词器一致）
        Analyzer analyzer = new StandardAnalyzer();

        //组合多个query条件
        //查询文件大小在1-20，并且文件名称为demo.txt
       BooleanQuery query = new BooleanQuery();

        //查询文件大小在1-20之间（包括1，包括20）
        Query numericRangeQuery = NumericRangeQuery.newLongRange("fileSize", 1L, 20L, true, true);

        Term term = new Term("fileName", "demo.txt");
        TermQuery termQuery = new TermQuery(term);

        query.add(numericRangeQuery, BooleanClause.Occur.MUST);
        query.add(termQuery, BooleanClause.Occur.MUST);

        queryIndexStore(query);

    }

    /***
     * 在多个域对象中查询关键字
     * @throws Exception
     */
    @Test
    public  void  testMultiFieldQueryParser() throws Exception {
        //创建分词器（必须和创建索引时的分词器一致）
        Analyzer analyzer = new StandardAnalyzer();

        String[] fields = {"fileName","fileContext"};

        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(fields, analyzer);

        Query query = multiFieldQueryParser.parse("world");

        queryIndexStore(query);
    }

    /**
     * 查询所有的文档
     * @throws Exception
     */
    @Test
    public void testMatchAllQuery() throws Exception {
        //创建分词器（必须和创建索引时的分词器一致）
        Analyzer analyzer = new StandardAnalyzer();

        MatchAllDocsQuery query = new MatchAllDocsQuery();

        queryIndexStore(query);
    }



    private void queryIndexStore(Query query) throws IOException {
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
