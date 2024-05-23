import React, {ReactNode, useEffect, useState } from "react";
import axios from "axios";
import { Table, Space, Input, Form, Button, Select } from "antd";
import { SearchOutlined } from "@ant-design/icons";
import config from 'config/config';
import './custom.css';

// Define the structure of your Elasticsearch document
interface ElasticsearchDocument {
  title: string;
  published_date: string;
  authors: string[];
  url: string;
}
interface QuotesListPageProps {
    children?: ReactNode; // If you intend to accept children, you need to define it in your props
  }

  export const QuotesListPage: React.FC<QuotesListPageProps> = ({ children }) => {
    const [documents, setDocuments] = useState<ElasticsearchDocument[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedElasticIndex, setSelectedElasticIndex] = useState("cnn");
  const [topicId, setTopicId] = useState(3);

  useEffect(() => {
    const fetchDocuments = async () => {
      const apiUrl = config.API_URL;
      setLoading(true);
      try {
        const response = await axios.get(`${apiUrl}api/news/by-topic`, {
          params: {
            startDate: '2023-01-01',
            endDate: '2023-12-31',
            topicId: topicId,
            index: `${selectedElasticIndex}_articles_new_news_topics`,
            searchTerm: searchTerm
          },
        });
        setDocuments(response.data);
      } catch (error) {
        console.error('Error fetching documents:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDocuments();
  }, [searchTerm, selectedElasticIndex, topicId]); // Added topicId as a dependency

  const handleElasticIndexChange = (value: string) => {
    setSelectedElasticIndex(value);
    setTopicId(value === "cnn" ? 3 : 0); // Update topicId based on index
  };

    const columns = [
        {
        title: "Title",
        dataIndex: "title",
        key: "title",
        },
        {
        title: "Published Date",
        dataIndex: "date",
        key: "date",
        render: (text: string | undefined) => text || "Date not available"
        },
        {
        title: "URL",
        dataIndex: "url",
        key: "url",
        render: (url: string) => <a href={url} target="_blank" rel="noopener noreferrer">Link</a>,
        },
    ];



    return (
        <div className="page-container">
            <header style={{ 
                padding: '20px', 
                backgroundColor: '#4e73df',
                color: 'white',
                textAlign: 'center', 
                marginBottom: '20px', 
                borderRadius: '5px',
                boxShadow: '0 4px 8px 0 rgba(0,0,0,0.2)'
            }}>
                <h1 style={{ 
                    margin: 0, 
                    fontWeight: 'normal',
                    fontSize: '26px' // Larger font size for main title
                }}>
                    News Bias Analysis Dashboard
                </h1>
                <p style={{
                    marginTop: '10px',
                    fontSize: '16px' // Smaller font size for subtitle
                }}>
                    Analyzing CNN and FOX News Articles  (1 Oct 2023 - 31 Dec 2023)<br/>
                    Focusing on the Israel-Palestine Conflict
                </p>
            </header>
            <div>
            
            </div>
        <Form layout="inline" style={{ marginBottom: 26 }}>
            <Form.Item>
            <Input
                prefix={<SearchOutlined />}
                placeholder="Search..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
            />
            </Form.Item>
            <Form.Item>
            <Button type="primary" onClick={() => setSearchTerm('')}>Clear</Button>
            </Form.Item>
        </Form>

        <Select  defaultValue={selectedElasticIndex} style={{ width: 120 ,marginBottom: 26 }} onChange={handleElasticIndexChange}>
                    <Select.Option value="cnn">cnn </Select.Option>
                    <Select.Option value="fox">fox</Select.Option>
                    {/* Add other options as needed */}
                </Select>
        <Table 
        
            dataSource={documents.map((doc, index) => ({ ...doc, key: index.toString() }))}
            columns={columns}
            loading={loading}
            pagination={{ pageSize: 30 }}
            
            />
        {children}
        </div>
    );
    };
