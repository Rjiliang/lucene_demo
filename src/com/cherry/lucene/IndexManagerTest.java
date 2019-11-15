package com.cherry.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

public class IndexManagerTest {

    /**
     * 创建索引测试
     * 采集文件系统中的文件放入lucene中
     * @throws Exception
     */
    @Test
    public void testIndexCreate() throws Exception{
        ArrayList<Document> docList = new ArrayList<>();
        //指定文件所在目录
        File dir = new File("E:\\git\\lucene_demo\\testFile");

        for (File file : dir.listFiles()) {
            //文件名称
            String fileName = file.getName();
            //文件内容
            String fileContext = FileUtils.readFileToString(file);
            //文件大小
            Long fileSize = FileUtils.sizeOf(file);

            //创建文档对象
            Document document = new Document();
            //创建域
            TextField nameField = new TextField("fileName", fileName, Field.Store.YES);
            TextField contextField = new TextField("fileContext", fileContext, Field.Store.YES);
            LongField sizeField = new LongField("fileSize", fileSize, Field.Store.YES);

            //将创建的所有域都存入文档中
            document.add(nameField);
            document.add(contextField);
            document.add(sizeField);

            //将文档存入文档集合中
            docList.add(document);
        }

        //创建分词器，StandardAnalyzer为标准分词器，中文单字分词
        Analyzer analyzer = new StandardAnalyzer();

        //指定索引库的位置
        Directory directory = FSDirectory.open(new File("E:\\git\\lucene_demo\\index"));
        //创建写对象的初始化配置
        IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LATEST, analyzer);
        //创建写对象
        IndexWriter indexWriter = new IndexWriter(directory, writerConfig);

        //将文档加入写对象中
        for (Document document : docList) {
            indexWriter.addDocument(document);
        }

        //提交写入索引库
        indexWriter.commit();

        //关闭流
        indexWriter.close();
    }

    /**
     * 删除索引测试
     * @throws Exception
     */
    @Test
    public void testIndexDeleted() throws Exception {

        //创建分词器，StandardAnalyzer为标准分词器，中文单字分词
        Analyzer analyzer = new StandardAnalyzer();

        //指定索引库的位置
        Directory directory = FSDirectory.open(new File("E:\\git\\lucene_demo\\index"));
        //创建写对象的初始化配置
        IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        //创建写对象
        IndexWriter indexWriter = new IndexWriter(directory, writerConfig);

        //删除索引库中所有索引
        //indexWriter.deleteAll();

        indexWriter.deleteDocuments(new Term("fileName","demo.txt"));


        //提交
        indexWriter.commit();
        indexWriter.close();

    }

    /**
     * 测试更新索引
     */
    @Test
    public void testIndexUpdate() throws Exception {
        //创建分词器，StandardAnalyzer为标准分词器，中文单字分词
        Analyzer analyzer = new StandardAnalyzer();

        //指定索引库的位置
        Directory directory = FSDirectory.open(new File("E:\\git\\lucene_demo\\index"));
        //创建写对象的初始化配置
        IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        //创建写对象
        IndexWriter indexWriter = new IndexWriter(directory, writerConfig);


        //更新条件
        Term term = new Term("fileName", "demo.txt");
        //新的文档
        Document document = new Document();
        document.add(new TextField("fileName","newFile", Field.Store.YES));
        document.add(new TextField("fileContext","hello new file", Field.Store.YES));
        document.add(new LongField("fileSize",10L, Field.Store.YES));
        //更新文档：按照传入的Term进行搜索，找到结果那么删除,将更新的内容重新生成一个Document对象，没有搜索到结果直接添加新的document
        indexWriter.updateDocument(term,document);

        //提交
        indexWriter.commit();
        //关闭
        indexWriter.close();
    }
}
