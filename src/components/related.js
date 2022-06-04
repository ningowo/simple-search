import React from "react";
import { Button, Row,Col } from "antd";
function Related() {
  const data = [
    {
      content: "Ant Design Title 1",
    },
    {
      content: "Ant Design Title 2",
    },
    {
      content: "Ant Design Title 3",
    },
    {
      content: "Ant Design Title 4",
    },
    {
      content: "Ant Design Title 5",
    },
    {
      content: "Ant Design Title 6",
    },
    {
      content: "Ant Design Title 7",
    },
    {
      content: "Ant Design",
    },
  ];
  return (
    <div>
      <h1>相关搜索</h1>
      <Row>
        {data.map((item) => (
          <Col  lg={{ span: 8}} md={{ span: 12}} xs={{ span: 24}}>
            <Button shape="round" icon="search" style={{width:200,margin:5,textAlign:"left"}}>
              {item.content}
            </Button>
          </Col>
        ))}
      </Row>
    </div>
  );
}
export default Related;
