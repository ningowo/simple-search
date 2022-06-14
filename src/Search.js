import React, { Component } from "react";
import {
  Radio,
  Row,
  Divider,
  Input,
  Pagination,
  Button,
  notification,
} from "antd";
import { addFav, showFav } from "./util/APICollect";
import Text from "./components/Text";
import Pic from "./components/Pic";
import "./App.css";
import Related from "./components/Related";

import { relatedSearchList } from "./util/Helpers";
import { search, filterWord } from "./util/APIUtils";
class Search extends Component {
  constructor(props) {
    super(props);
    this.state = {
      searchOpt: "text",
      docVOList: null,
      relatedSearchList: relatedSearchList,
      filter: "",
      query: "1",
      page: { pageSize: 10, pageNum: 1, total: 0 },
      isLoading: true,
      AllData: null,
      userId: "",
    };

    // 为了在回调中使用 `this`，这个绑定是必不可少的
    this.onChangeOption = this.onChangeOption.bind(this);
    this.onClickAddFav = this.onClickAddFav.bind(this);
    this.onFilter = this.onFilter.bind(this);
    this.onSearch = this.onSearch.bind(this);
    this.onShowSizeChange = this.onShowSizeChange.bind(this);
  }
  onChangeOption = ({ target: { value } }) => {
    this.setState({ searchOpt: value });
  };
  onFilter = () => {
    this.setState({ isLoading: true });
    filterWord(this.state.query, [this.state.filter], 10, 1)
      .then((response) => {
        console.log("test", response);
        this.setState({
          docVOList: response.data.docVOList.slice(0, 10),
          relatedSearchList: response.data.relatedSearchList,
          page: response.data.query,
          isLoading: false,
          AllData: response.data.docVOList,
        });
      })
      .catch((error) => {
        notification.error({
          message: "Search App",
          description:
            error.message || "Sorry! Something went wrong. Please try again!",
        });
      });
  };
  onSearch = (value) => {
    search(value)
      .then((response) => {
        this.setState({
          // docVOList: response.data.docVOList.slice(0, 10),
          docVOList: response.data.docVOList,
          relatedSearchList: response.data.relatedSearchList,
          page: response.data.query,
          AllData: response.data.docVOList,
        });
      })
      .catch((error) => {
        notification.error({
          message: "Search App",
          description:
            error.message || "Sorry! Something went wrong. Please try again!",
        });
      });
  };
  onChange1 = (event) => {
    const { value: inputValue } = event.target;
    this.setState({
      query: inputValue,
    });
  };
  onChange2 = (event) => {
    const { value: inputValue } = event.target;
    this.setState({
      filter: inputValue,
    });
  };
  onClickAddFav(event) {
    const { key: inputValue } = event.target;
    console.log("onClickAddFav", inputValue);
    console.log("this.props.username", this.props.username);
    if (this.props.username !== null) {
      addFav(3, 2)
        .then((response) => {
          console.log(response);
          this.setState({
            data: response.data,
          });
          notification.success({
            message: "Search App",
            description: "You're successfully add favourite.",
          });
        })
        .catch((error) => {
          if (error.status === 404) {
            console.log("404");
          } else {
            showFav(3)
              .then((response) => {
                console.log("show Fav", response);
              })
              .catch((error) => {
                notification.error({
                  message: "Search App",
                  description:
                    error.message ||
                    "Sorry! Something went wrong. Please try again!",
                });
              });
          }
        });
    }
  }
  onShowSizeChange = (current, pageSize) => {
    // console.log("current, pageSize", current, pageSize);
    // var start = (current - 1) * pageSize;
    // var end = current * pageSize;
    // this.setState({
    //   // docVOList: this.state.AllData.slice(start, end),
    //   docVOList: this.state.AllData,
    // });
    filterWord(this.state.query, [this.state.filter], pageSize, current)
      .then((response) => {
        this.setState({
          docVOList: response.data.docVOList,
          relatedSearchList: response.data.relatedSearchList,
          page: response.data.query,
          isLoading: false,
          AllData: response.data.docVOList,
        });
      })
      .catch((error) => {
        notification.error({
          message: "Search App",
          description:
            error.message || "Sorry! Something went wrong. Please try again!",
        });
      });
  };
  componentDidMount() {
    if (this.props.location.state !== undefined) {
      this.setState({
        query: this.props.location.state.query,
        userId: this.props.location.state.userId,
      });
      console.log(this.props.location.state);
      console.log("this.props.match.userId", this.props.location.state.userId);
      this.onSearch(this.props.location.state.query);
    }
  }

  render() {
    const options = [
      { label: "文字", value: "text" },
      { label: "图片", value: "pic" },
    ];

    return (
      <div>
        <Input.Group compact>
          <Input style={{ width: "calc(40%)" }} onChange={this.onChange1} />
          <Input style={{ width: "calc(20%)" }} onChange={this.onChange2} />
          <Button type="primary" onClick={this.onFilter}>
            过滤搜索
          </Button>
        </Input.Group>
        <h1>Result</h1>
        <Divider style={{ marginTop: 10 }} />
        <Radio.Group
          options={options}
          onChange={this.onChangeOption}
          value={this.state.searchOpt}
          size="large"
          optionType="button"
          buttonStyle="solid"
          key={options.value}
          style={{ marginBottom: 20 }}
        />
        <div hidden={this.state.searchOpt === "pic"}>
          <Text data={this.state.docVOList} userId={this.state.userId} />
        </div>
        <Row gutter={16} center="lg" hidden={this.state.searchOpt === "text"}>
          <Pic
            data={this.state.docVOList}
            isLoading={this.state.isLoading}
            addFav={this.onClickAddFav}
            userId={this.state.userId} 
          />
        </Row>
        <div className="example">
          <Related
            data={this.state.relatedSearchList}
            onClick={this.onSearch}
          />
        </div>
        <div className="example">
          <Pagination
            total={this.state.page.total}
            showTotal={(total) => `Total ${total} items`}
            defaultPageSize={10}
            defaultCurrent={1}
            onChange={this.onShowSizeChange}
          />
        </div>
      </div>
    );
  }
}

export default Search;
