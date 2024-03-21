import React from "react";

import { Col, Row } from "antd";
import { Select } from 'antd';

import { CalendarUpcomingEvents } from "@/components";
import { useState } from "react";

import { DashboardTermsPercentageChart } from "./components/percentage-chart";
import { DashboardTermKeywordPercentageChart } from "./components/percentage-keyword";

import {
    CompaniesMap,
    DashboardDealsChart,
    DashboardLatestActivities,
    DashboardTasksChart,
    DashboardTotalCountCard,
    DashboardSentimentAnalysisChart
} from "./components";
import { DashBoardWordCloudChart } from "./components";
// import { DashboardBiasAnalysisChart } from "./components";

import { DashboardHighlights } from "./components/highlights";
import {configurl} from "./components/config";
import configa from 'config/config'; // Adjust the path as necessary
// import {DashboardBiasAnalysisChart} from "bias-chart";

export const DashboardPage: React.FC = () => {
    const [selectedElasticIndex, setSelectedElasticIndex] = useState("cnn");

    const handleElasticIndexChange = (value: string) => {
        setSelectedElasticIndex(value);
    };

    const [selectedKeyword, setSelectedKeyword] = useState("");
    const elastic_index = configa.INDEX_NAME;
    // Add type annotation for 'word' parameter
    const handleWordClick = (word: string) => {
        setSelectedKeyword(word);
        console.log("Selected keyword:", word); // Add this line to debug
    };

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
        <Select defaultValue={selectedElasticIndex} style={{ width: 120 }} onChange={handleElasticIndexChange}>
                <Select.Option value="cnn">cnn </Select.Option>
                <Select.Option value="fox">fox</Select.Option>
                {/* Add other options as needed */}
            </Select>
        </div>
        

              <Row
                gutter={[32, 32]}
                style={{
                    marginTop: "15px",
                    marginBottom: "32px"
                }}
            >
                <Col
                    xs={24}
                    sm={194}
                    xl={25}
                    style={{
                        height: "500px",
                    }}
                >

                    <DashboardTasksChart chart_title = "Overall news topics" indexAI = {`${selectedElasticIndex}_articles_new-records2`} description="Overall topics" />
                </Col>
                
             
            </Row>

            {/* <Row
                gutter={[32, 32]}
                style={{
                    marginTop: "2px",
                    marginBottom: "32px"
                }}
            >
                <Col
                    xs={24}
                    sm={194}
                    xl={25}
                    style={{
                        height: "500px",
                    }}
                >
                    <DashboardTasksChart chart_title = "Israel-Palestine news topics" indexAI = "cnn_gaza-records2" description="Topics about Israel-Palestine" />
                </Col>
                
             
            </Row> */}

                        <Row
                gutter={[32, 32]}
                style={{
                    marginTop: "2px",
                    marginBottom: "32px"
                }}
            >
                <Col
                    xs={24} // Full width on extra-small screens
                    sm={24} // Full width on small screens
                    md={12} // Half width on medium screens
                    lg={12} // Half width on large screens
                    xl={12} // Half width on extra-large screens, adjust if necessary
                    style={{
                        height: "532px",
                    }}
                >
                    <DashboardTermsPercentageChart index_name={`${selectedElasticIndex}`} keyword1="israel" keyword2="palestine" keyword3="genocide" chartTitle="Genocide context"/>
                </Col>
                <Col
                    xs={24} // Full width on extra-small screens
                    sm={24} // Full width on small screens
                    md={12} // Half width on medium screens
                    lg={12} // Half width on large screens
                    xl={12} // Half width on extra-large screens, adjust if necessary
                    style={{
                        height: "532px",
                    }}
                >
                    <DashboardTermsPercentageChart index_name={`${selectedElasticIndex}`} keyword1="israeli" keyword2="palestinian" keyword3="children" chartTitle="Children context"/>
                </Col>
            </Row>

            <Row
                gutter={[32, 32]}
                style={{
                    marginTop: "22px",
                    marginBottom: "22px"
                }}
            >
                {/* Other columns */}
                <Col
                    xs={24} // Full width on extra-small screens
                    sm={24} // Full width on small screens
                    md={12} // Half width on medium screens
                    lg={12} // Half width on large screens
                    xl={12} // Half width on extra-large screens, adjust if necessary
                    style={{
                        height: "532px",
                    }}
                >
                     <DashBoardWordCloudChart index_name={`${selectedElasticIndex}`} />
                </Col>
                <Col
                    xs={24} // Full width on extra-small screens
                    sm={24} // Full width on small screens
                    md={12} // Half width on medium screens
                    lg={12} // Half width on large screens
                    xl={12} // Half width on extra-large screens, adjust if necessary
                    style={{
                        height: "532px",
                    }}
                >
                    <DashboardTermKeywordPercentageChart index_name={`${selectedElasticIndex}`} keyword1="israel" keyword2="palestine"  chartTitle="Israel-Palestine ratio"/>
                </Col>
            </Row>
            <Row gutter={[32, 32]}>
                {/* <Col xs={24} sm={24} xl={8}>
                    <DashboardTotalCountCard resource="companies" />
                </Col>
                <Col xs={24} sm={24} xl={8}>
                    <DashboardTotalCountCard resource="contacts" />
                </Col> */}
                <Col xs={24} sm={24} xl={12}>
                <DashboardSentimentAnalysisChart
                    sentiment_index={`${selectedElasticIndex}`}
                    index ="sentiment-analysis-results"
                    term = "Palestine"
                    chart_title="Sentiment Analysis on term 'Palestine' "
                    description="-1 as very Negative and 1 as very Positive"
                />

                </Col>
                <Col xs={24} sm={24} xl={8}>
                <DashboardSentimentAnalysisChart
                    sentiment_index={`${selectedElasticIndex}`}
                    index ="sentiment-analysis-results"
                    term = "Hamas"
                    chart_title="Sentiment Score on term 'Hamas' "
                    description="-1 as very Negative and 1 as very Positive"
                />

                </Col>

            </Row>

            {/* <Row gutter={[32, 32]}>
         
                <Col xs={24} sm={24} xl={12}>
                <DashboardSentimentAnalysisChart
                    sentiment_index={`${selectedElasticIndex}`}
                    index ="bias-analysis-results"
                    term = "Biden"
                    chart_title="Bias Analysis on documents about 'Biden' "
                    description="-1 as progressive left and 1 as progressive right"
                />

                </Col>
                <Col xs={24} sm={24} xl={8}>
                <DashboardSentimentAnalysisChart
                    sentiment_index={`${selectedElasticIndex}`}
                    index ="bias-analysis-results"
                    term = "Hamas"
                    chart_title="Bias Analysis on documents about 'Hamas' "
                    description="-1 as progressive left and 1 as progressive right"
                />

                </Col>

            </Row> */}

            <Row gutter={[16, 32]} style={{ marginTop: "32px" }}>
                <Col
                    xs={24} // Full width on extra-small screens
                    sm={24} // Full width on small screens
                    md={12} // Half width on medium screens
                    lg={12} // Half width on large screens
                    xl={12} // Half width on extra-large screens, adjust if necessary
                    style={{
                        height: "432px",
                    }}
                >
                    <DashboardDealsChart index_name={`${selectedElasticIndex}`} selectedKeyword="israeli" chartTitle="Israeli keyword"/>
                </Col>
                <Col
                    xs={24} // Full width on extra-small screens
                    sm={24} // Full width on small screens
                    md={12} // Half width on medium screens
                    lg={12} // Half width on large screens
                    xl={12} // Half width on extra-large screens, adjust if necessary
                    style={{
                        height: "432px",
                    }}
                >
                    <DashboardDealsChart index_name={`${selectedElasticIndex}`} selectedKeyword="palestinian" chartTitle="Palestinian keyword" />
                </Col>
            </Row>



            <Row
            gutter={[32, 32]}
            style={{
                marginTop: "82px",
            }}
        >
                {/* <Col  xs={24} // Full width on extra-small screens
                    sm={24} // Full width on small screens
                    md={12} // Half width on medium screens
                    lg={12} // Half width on large screens
                    xl={12}>
                <DashboardLatestActivities index_name={`${selectedElasticIndex}`} />
            </Col>
             */}
            <Col xs={24} sm={24} xl={12} xxl={16}>
                <DashboardLatestActivities index_name={`${selectedElasticIndex}`} />
            </Col>
            
            {/* If you ever want to include a third column in the future, ensure the total of xl and xxl values does not exceed 24 */}
            {/* <Col xs={24} sm={24} xl={10} xxl={8}>
                <CalendarUpcomingEvents showGoToListButton />
            </Col> */}
        </Row>

            <Row gutter={[32, 32]} style={{ marginTop: "32px" }}>
                <Col xs={24} sm={24} xl={12} xxl={12}>
                    <DashboardHighlights index_name={`${selectedElasticIndex}`} keyword1="palestinian" keyword2="children" chartTitle="Palestinian Children context" />
                </Col>
                <Col xs={24} sm={24} xl={12} xxl={12}>
                    <DashboardHighlights index_name={`${selectedElasticIndex}`} keyword1="israeli" keyword2="children" chartTitle="Israeli Children context" />
                </Col>
            </Row>
           

          
        </div>
    );
};
