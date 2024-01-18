import React, { useState, useEffect, lazy, Suspense } from "react";
import axios from 'axios';
import { PieConfig } from "@ant-design/plots";
import { Card, Tooltip } from "antd";
import { ProjectOutlined } from "@ant-design/icons";
import { Text } from "@/components";
import configa from 'config/config'; // Adjust the path as necessary

const Pie = lazy(() => import("@ant-design/plots/es/components/pie"));

// Define the structure of a task
interface Task {
    title: string;
    value: number;
}

export const DashboardTasksChart: React.FC<{chart_title: string, indexAI: string, description: string}> = ({chart_title, indexAI, description}) => {
    const [tasksData, setTasksData] = useState<Task[]>([]);
    const apiUrl = configa.API_URL;

    useEffect(() => {
        console.log(indexAI);
        axios.get(`${apiUrl}api/news/openai-data?indexName=${indexAI}`)
            .then(response => {
                const transformedData = response.data
                    .map((item: { openAI: string, count: number }) => ({
                        title: item.openAI,
                        value: item.count
                    }))
                    .sort((a: Task, b: Task) => b.value - a.value); // Sorting data by value in descending order
                setTasksData(transformedData);
            })
            .catch(error => console.error("Error fetching task chart data", error));
    }, [indexAI]);

    const COLORS = [
        "#BAE0FF", "#69B1FF", "#1677FF", "#0958D9", "#10239E",
        "#061178", "#030852", "#03052E", "#000B0A", "#000000",
    ];

    const config: PieConfig = {
        padding: [10, 20, 50, 20], // Example: top, right, bottom, left padding

        width: 1068,
        height: 368,
        data: tasksData,
        angleField: "value",
        colorField: "title",
        color: COLORS,
        legend: false,
        radius: 1,
        innerRadius: 0.4,
        label: {
            
            type: 'outer',
            content: (data, item, index) => {
                if (index>=0 &&index < 5) {
                    return `${data.title}: ${data.value}`;
                }
                return '';
            },
        },
        syncViewPadding: true,
        
        interactions: [
            {
              type: 'pie-legend-active',
            },
            {
              type: 'element-active',
            },
          ],
        statistic: {
            title: false,
            content: false,
        },
    };

    return (
        <Card
            style={{ display: 'flex', flexDirection: 'column', height: '100%', paddingLeft:'30px' }}
            headStyle={{ padding: '16px 16px' }}
            bodyStyle={{ display: 'flex', padding: '50px', justifyContent: 'flex-start' }}
            title={
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                    <ProjectOutlined />
                    <Tooltip title={description}>
                        <Text size="sm" style={{ marginLeft: '.5rem', cursor: 'pointer' }}>{chart_title}</Text>
                    </Tooltip>
                </div>
            }
        >
            <div style={{ flex: 1, display: 'flex', justifyContent: 'flex-start' }}>
                <Suspense fallback={<div>Loading...</div>}>
                    <Pie {...config} />
                </Suspense>
            </div>
            <div
                style={{
                    flex: 1,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'flex-start',
                    marginLeft: '1px',
                }}
            >
                {tasksData.slice(0, 10).map((item, index) => (
                    <div
                        key={index}
                        style={{
                            display: "flex",
                            alignItems: "center",
                            marginBottom: "8px",
                        }}
                    >
                        <div
                            style={{
                                width: 18,
                                height: 28,
                                backgroundColor: COLORS[index % COLORS.length],
                                marginRight: ".5rem",
                            }}
                        />
                        <Text
                            size="md"
                            style={{
                                textTransform: "capitalize",
                                whiteSpace: "nowrap",
                            }}
                        >
                            {item.title}
                        </Text>
                    </div>
                ))}
            </div>
        </Card>
    );
};