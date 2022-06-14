import React, { Component } from "react";
import {
  delFav,
  addFolder,
  showFav,
  showFolder,
  delFolder,
  renameFolder,
} from "../util/APICollect";
import {
  Divider,
  Card,
  Input,
  Skeleton,
  Icon,
  Row,
  Col,
  Button,
  notification,
} from "antd";
const { Meta } = Card;
class Collect extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isLoading: false,
      data: null,
      id: 0,
      favList: null,
      favNameId: "",
      rename: "",
      hideInput: true,
    };
    this.clickAddFloder = this.clickAddFloder.bind(this);
    this.clickdeleteFav = this.clickdeleteFav.bind(this);
    this.onChange1 = this.onChange1.bind(this);
    this.onChange2 = this.onChange2.bind(this);
    this.changeFolder = this.changeFolder.bind(this);
    this.clickdeleteFolder = this.clickdeleteFolder.bind(this);
    this.clickRenameFolder = this.clickRenameFolder.bind(this);
  }
  loadUserProfile(userId) {
    showFolder(userId)
      .then((response) => {
        console.log(response);
        this.setState({
          data: response.data,
        });
        showFav(this.state.data[0].id)
          .then((response) => {
            console.log("hhh", response);
            this.setState({
              favList: response.data,
              favNameId: this.state.data[0].id,
            });
          })
          .catch((error) => {
            if (error.status === 404) {
              console.log("404");
            } else {
              console.log("success");
              this.forceUpdate();
            }
          });
      })
      .catch((error) => {
        if (error.status === 404) {
          console.log("404");
        }
      });
  }
  clickAddFloder() {
    addFolder(this.props.id, this.state.name)
      .then((response) => {
        showFolder(this.state.id)
          .then((response) => {
            console.log(response);
            this.setState({
              data: response.data,
            });
            notification.success({
              message: "Search App",
              description: "You're successfully add favourite folder.",
            });
          })
          .catch((error) => {
            if (error.status === 404) {
              console.log("404");
            } else {
              console.log("success");
            }
          });
      })
      .catch((error) => {
        if (error.status === 404) {
          console.log("404");
        } else {
          console.log("success");
        }
      });
  }
  onChange1 = (event) => {
    const { value: inputValue } = event.target;
    this.setState({
      name: inputValue,
    });
  };
  onChange2 = (event) => {
    const { value: inputValue } = event.target;
    this.setState({
      rename: inputValue,
    });
  };
  clickRenameFolder(favouriteName) {
    renameFolder(this.state.id, favouriteName, this.state.rename)
      .then((response) => {
        showFolder(this.state.id)
          .then((response) => {
            console.log(response);
            this.setState({
              data: response.data,
            });
            notification.success({
              message: "Search App",
              description: "You're successfully add favourite folder.",
            });
          })
          .catch((error) => {
            if (error.status === 404) {
              console.log("404");
            } else {
              console.log("success");
            }
          });
      })
      .catch((error) => {
        if (error.status === 404) {
          console.log("404");
        } else {
          console.log("success");
        }
      });
    this.setState({ hideInput: true });
  }
  changeFolder(favId) {
    this.setState({ favNameId: favId });
    showFav(favId)
      .then((response) => {
        this.setState({
          favList: response.data,
        });
      })
      .catch((error) => {
        if (error.status === 404) {
          console.log("404");
        } else {
          console.log("success");
        }
      });
  }
  clickdeleteFav(res) {
    console.log(res);
    delFav(this.state.favNameId, res.docId)
      .then((response) => {
        showFav(this.state.favNameId)
          .then((response) => {
            this.setState({
              favList: response.data,
            });
            notification.success({
              message: "Search App",
              description: "取消收藏 You're successfully delete favourite.",
            });
          })
          .catch((error) => {
            if (error.status === 404) {
              console.log("404");
            } else {
              console.log("success");
            }
          });
      })
      .catch((error) => {
        if (error.status === 404) {
          console.log("404");
        } else {
          console.log("success");
          this.forceUpdate();
        }
      });
  }
  clickdeleteFolder(res) {
    delFolder(this.state.id, res.favouriteName)
      .then((response) => {
        showFolder(this.state.id)
          .then((response) => {
            console.log(response);
            this.setState({
              data: response.data,
            });
            notification.success({
              message: "Search App",
              description: "You're successfully delete favourite folder.",
            });
          })
          .catch((error) => {
            if (error.status === 404) {
              console.log("404");
            } else {
              console.log("success");
            }
          });
      })
      .catch((error) => {
        if (error.status === 404) {
          console.log("404");
        } else {
          console.log("success");
          this.forceUpdate();
        }
      });
  }
  componentDidMount() {
    const userId = this.props.id;
    this.setState({
      id: userId,
    });
    this.loadUserProfile(userId);
  }

  render() {
    const gridStyle = {
      width: "calc(30%)",
      textAlign: "center",
      fontSize: "0.8rem",
    };

    return (
      <div>
        <div>
          <div>
            <div style={{ padding: 10 }}>
              <Card title="我的收藏夹">
                {!this.state.data
                  ? null
                  : this.state.data.map((res, index) => (
                      <Card.Grid
                        style={gridStyle}
                        onClick={() => {
                          this.changeFolder(res.id);
                        }}
                      >
                        <Meta title={res.favouriteName}></Meta>
                        <Button
                          icon="delete"
                          style={{ marginTop: 10 }}
                          onClick={() => {
                            this.clickdeleteFolder(res);
                          }}
                        />
                        <Button
                          icon="check"
                          style={{ marginTop: 10 }}
                          onClick={() => {
                            this.changeFolder(res.id);
                          }}
                        />
                        <Button
                          icon="edit"
                          style={{ marginTop: 10 }}
                          onClick={() => {
                            this.setState({
                              hideInput: !this.state.hideInput,
                            });
                          }}
                        />
                        <Input.Group compact style={{ marginTop: 10 }}>
                          <Input
                            hidden={this.state.hideInput}
                            style={{ width: "calc(40%)" }}
                            onChange={this.onChange2}
                          />
                          <Button
                            icon="plus"
                            onClick={() => {
                              this.clickRenameFolder(res.favouriteName);
                            }}
                            hidden={this.state.hideInput}
                          />
                        </Input.Group>
                      </Card.Grid>
                    ))}
                <Card.Grid style={gridStyle}>
                  <Meta title="添加收藏夹"></Meta>
                  <Input.Group compact style={{ marginTop: 10 }}>
                    <Input
                      style={{ width: "calc(40%)" }}
                      onChange={this.onChange1}
                    />
                    <Button icon="plus" onClick={this.clickAddFloder} />
                  </Input.Group>
                </Card.Grid>
              </Card>
            </div>
          </div>
          <div>
            <h1>我的收藏</h1>
            <Divider style={{ marginTop: 10 }} />
            <div style={{ padding: 10 }}>
              <Row gutter={16}>
                {!this.state.favList ? (
                  <h5>当前收藏夹没有内容</h5>
                ) : (
                  this.state.favList.map((res, index) => (
                    <Col
                      lg={{ span: 4 }}
                      sm={{ span: 12 }}
                      xs={{ span: 24 }}
                      key={index}
                    >
                      <Card
                        style={{ marginBottom: 16 }}
                        cover={
                          <img
                            alt="favourite"
                            src={res.url}
                            style={{ height: 200 }}
                          />
                        }
                        actions={[
                          <Icon
                            type="delete"
                            key="delete"
                            onClick={() => {
                              this.clickdeleteFav(res);
                            }}
                          />,
                        ]}
                      >
                        <Skeleton loading={this.props.isLoading} avatar active>
                          <Meta
                            style={{ height: 50 }}
                            description={res.caption}
                          />
                        </Skeleton>
                      </Card>
                    </Col>
                  ))
                )}
              </Row>
            </div>
          </div>
        </div>
      </div>
    );
  }
}

export default Collect;
