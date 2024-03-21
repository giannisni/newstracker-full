import React, { useEffect, useState } from "react";
import axios from "axios";
import { Table } from "antd";
import configa from 'config/config'; // Adjust the path as necessary

// Define the structure of your document data
interface Document {
  title: string;
  text: string;
  authors: string[];
  published_date: string;
  url: string;
}

export const DocumentTable: React.FC = () => {
  const [documents, setDocuments] = useState<Document[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchDocuments = async () => {
      const startDate = "2023-01-01";
      const endDate = "2023-12-31";
      const topicId = 1; // Assuming you're fetching by a specific topic ID
      const index = "cnn_articles_new_news_topics";

      const apiUrl = configa.API_URL;

      setLoading(true);
      try {
        const response = await axios.get(`${apiUrl}api/news/by-topic`, {
          params: { startDate, endDate, topicId, index },
        });
        setDocuments(response.data); // Assuming the response body will be the list of documents
      } catch (error) {
        console.error('Error fetching documents:', error);
      } finally {
        setLoading(false);
      }
    };
  
    fetchDocuments();
  }, []);
  

  const columns = [
    {
      title: "Title",
      dataIndex: "title",
      key: "title",
    },
    {
      title: "Published Date",
      dataIndex: "published_date",
      key: "published_date",
    },
    {
      title: "Authors",
      dataIndex: "authors",
      key: "authors",
      render: (authors: string[]) => authors.join(", "),
    },
    {
      title: "URL",
      dataIndex: "url",
      key: "url",
      render: (url: string) => <a href={url} target="_blank" rel="noopener noreferrer">Link</a>,
    },
  ];

  return (
    <Table
      dataSource={documents}
      columns={columns}
      loading={loading}
      rowKey="url" // assuming the URL is unique for each document
      pagination={{ pageSize: 20 }}
    />
  );
};

export default DocumentTable;
