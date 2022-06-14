import React, { Component } from "react";
import { Input } from "antd";
import { Link, withRouter } from "react-router-dom";
import "./index.css";

class Home extends Component {
  constructor(props) {
    super(props);
    this.state = {
      query: '',
      userId:'',
    };
    this.onSearch = this.onSearch.bind(this);
  }
  onSearch = (event) => {
    const { value: inputValue } = event.target;
    this.setState({
      query:inputValue
    });
  }
  componentDidMount() {
    console.log("home",this.props)
    if(this.props.location.state){
      this.setState({
        userId:this.props.location.state.userId
      });
    }
  }
  render() {
    return (
      <div className="App-content">
        <div className="blank">
          <div className="logo">
            <img
              src="https://p6-passport.byteacctimg.com/img/user-avatar/b1fd2e8dfe7e81172d3ca830fa7788bb~300x300.image"
              alt="logo"
              className="logoimg"
            ></img>
          </div>
          <div className="search">
            <div className="searchbar">
              <Input type="text" className="searchMsg" onChange={this.onSearch}/>
              <Link to={{pathname: '/search', state: {query: this.state.query,userId:this.state.userId}}}>
                <span className="btn">
                  搜索关键词
                </span>
              </Link>
            </div>
          </div>
        </div>
      </div>
    );
  }
}

export default withRouter(Home);
