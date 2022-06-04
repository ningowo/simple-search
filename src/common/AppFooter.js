import React, { Component } from "react";
import { withRouter } from "react-router-dom";
import "./AppFooter.css";
import "antd/dist/antd.css";
import { Row, Col } from "antd";
class AppHeader extends Component {
  constructor(props) {
    super(props);
    this.handleMenuClick = this.handleMenuClick.bind(this);
  }

  handleMenuClick({ key }) {
    if (key === "logout") {
      this.props.onLogout();
    }
  }

  render() {
    return (
      <footer id="footer" className="dark">
        <div className="footer-wrap">
          <div style={{ display: "flex", flexDirection: "row" }}>
            <div>
              <p style={{ fontSize: 18, color: "#fff" }}>
                Search App
              </p>
              <p style={{ fontSize: 12, color: "#fff", marginLeft: 5 }}>
                  [ simple - serach ]
                </p>
            </div>
          </div>
        </div>
        <Row className="bottom-bar">
          <Col lg={6} sm={24}>
            <span
              style={{
                lineHeight: "16px",
                paddingRight: 12,
                marginRight: 11,
                borderRight: "1px solid rgba(255, 255, 255, 0.55)",
              }}
            >
              <a
                href="https://sx3c136k56.feishu.cn/docs/doccnGKKcca8JJRoqLfV2taixQe#"
                rel="noopener noreferrer"
                target="_blank"
              >
                项目文档
              </a>
            </span>
            <span style={{ marginRight: 24 }}>
              <a
                href="https://github.com/ningowo/simple-search"
                rel="noopener noreferrer"
                target="_blank"
              >
                代码地址
              </a>
            </span>
          </Col>
          <Col lg={18} sm={24}>
            <span style={{ marginRight: 12 }}>青训营</span>
            <span style={{ marginRight: 12 }}>Copyright ©</span>
          </Col>
        </Row>
      </footer>
    );
  }
}

export default withRouter(AppHeader);
