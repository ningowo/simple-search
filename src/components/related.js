import React from "react";
import { Button, Row, Col } from "antd";
function Related(props) {
  return (
    <div>
      <h1>相关搜索</h1>
      <Row>
        {props.data.map((item) => (
         
          <Col lg={{ span: 8 }} md={{ span: 12 }} xs={{ span: 24 }}  >
              <Button
                icon="search"
                style={{ width: 200, margin: 5, textAlign: "left" }}
                onClick={() => { props.onClick(item) }}
              >
                {item}
              </Button>
          </Col>
        ))}
      </Row>
    </div>
  );
}
export default Related;
