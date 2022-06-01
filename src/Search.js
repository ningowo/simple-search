import React, { Component, useState } from "react";
import { Radio, Row, Card, Col } from "antd";
import Text from "./components/Text";
import "./App.css";
class Search extends Component {
  constructor(props) {
    super(props);
    this.state = { searchOpt: "text" };

    // 为了在回调中使用 `this`，这个绑定是必不可少的
    this.onChangeOption = this.onChangeOption.bind(this);
  }
  onChangeOption = ({ target: { value } }) => {
    this.setState({ searchOpt: value });
  };
  onSearch = (event) => {
    console.log("event");
  };

  render() {
    const options = [
      { label: "文字", value: "text" },
      { label: "图片", value: "pic" },
    ];
    return (
      <div style={{ marginTop: 60 }}>
        <h1>Result</h1>
        <Radio.Group
          options={options}
          onChange={this.onChangeOption}
          value={this.state.searchOpt}
          size="large"
          optionType="button"
          buttonStyle="solid"
          key={options.value}
        />
        <div hidden={this.state.searchOpt === "pic"}>
          <Text />
        </div>

        <Row gutter={16} center="lg" hidden={this.state.searchOpt === "text"}>
          <Col span={8}>
            <Card
              hoverable
              style={{ width: 240 }}
              cover={
                <img
                  alt="example"
                  src="https://os.alipayobjects.com/rmsportal/QBnOOoLaAfKPirc.png"
                />
              }
            ></Card>
          </Col>
        </Row>
      </div>
    );
  }
}

export default Search;