import React, { Component } from "react";
import { getUserProfile } from "../../util/APIUtils";
import { Divider, Card, Skeleton, Icon, Row, Col, Button } from "antd";
import LoadingIndicator from "../../common/LoadingIndicator";
import NotFound from "../../common/NotFound";
import CheckAuthentication from "../../common/CheckAuthentication";
import ServerError from "../../common/ServerError";
const { Meta } = Card;
class Collect extends Component {
  constructor(props) {
    super(props);
    this.state = {
      user: null,
      isLoading: false,
    };
  }
  loadUserProfile(username) {
    if (this.props.username !== null) {
      this.setState({
        isLoading: true,
      });

      getUserProfile(username)
        .then((response) => {
          this.setState({
            user: response,
            isLoading: false,
          });
        })
        .catch((error) => {
          if (error.status === 404) {
            this.setState({
              notFound: true,
              isLoading: false,
            });
          } else {
            this.setState({
              serverError: true,
              isLoading: false,
            });
          }
        });
    }
  }

  componentDidMount() {
    const username = this.props.match.params.username;
    this.loadUserProfile(username);
  }

  componentWillReceiveProps(nextProps) {
    if (this.props.match.params.username !== nextProps.match.params.username) {
      this.loadUserProfile(nextProps.match.params.username);
    }
  }

  render() {
    if (this.state.isLoading) {
      return <LoadingIndicator />;
    }

    if (this.state.notFound) {
      return <NotFound />;
    }

    if (this.state.serverError) {
      return <ServerError />;
    }
    if (!this.props.isAuthenticated) {
      return <CheckAuthentication {...this.props} />;
    }
    const gridStyle = {
      width: "25%",
      textAlign: "center",
    };
    const dropList = [
      {
        id: 14,
        userId: 1,
        favouriteName: "baidu",
      },
      {
        id: 11,
        userId: 1,
        favouriteName: "oracle",
      },
    ];
    const myFav = [
      {
        id: 1,
        url: "https://gimg2.baidu.com/image_search/src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20200326%2Fffc00cb6bc944e5b9ab2673c4873b24c.jpeg&refer=http%3A%2F%2F5b0988e595225.cdn.sohucs.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1632531279&t=1b9ba84f70ddebdda6601a5576d37c50",
        caption: "美沃可视数码裂隙灯,检查眼前节健康状况",
      },
      {
        id: 2,
        url: "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fcbu01.alicdn.com%2Fimg%2Fibank%2F2020%2F527%2F038%2F17187830725_1528924397.220x220.jpg&refer=http%3A%2F%2Fcbu01.alicdn.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1632524815&t=d66159b43fb0335c11898f9764847ea7",
        caption: "欧美夏季ebay连衣裙 气质圆领通勤绑带收腰连衣裙 zc3730",
      },
      {
        id: 3,
        url: "https://pic.rmb.bdstatic.com/19539b3b1a7e1daee93b0f3d99b8e795.png",
        caption: "曾是名不见经传的王平,为何能够取代魏延,成为蜀汉",
      },
      {
        id: 3,
        url: "https://pic.rmb.bdstatic.com/19539b3b1a7e1daee93b0f3d99b8e795.png",
        caption: "曾是名不见经传的王平,为何能够取代魏延,成为蜀汉",
      },
      {
        id: 3,
        url: "https://pic.rmb.bdstatic.com/19539b3b1a7e1daee93b0f3d99b8e795.png",
        caption: "曾是名不见经传的王平,为何能够取代魏延,成为蜀汉",
      },
      {
        id: 3,
        url: "https://pic.rmb.bdstatic.com/19539b3b1a7e1daee93b0f3d99b8e795.png",
        caption: "曾是名不见经传的王平,为何能够取代魏延,成为蜀汉",
      },
    ];
    return (
      <div className="profile">
        {this.state.user ? (
          <div>
            <div className="home-page-wrapper">
              <div className="home-page-card" style={{ padding: 10 }}>
                <Card title="我的收藏夹">
                  {dropList.map((res, index) => (
                    <Card.Grid style={gridStyle}>
                      <Meta title={res.favouriteName}></Meta>
                      <Button>delete</Button>
                    </Card.Grid>
                  ))}
                  <Card.Grid style={gridStyle}>
                    <Meta title="添加收藏夹" onClick={this.showModal}></Meta>
                  </Card.Grid>
                </Card>
              </div>
            </div>
            <div className="home-page-wrapper">
              <h1>我的收藏</h1>
              <Divider style={{ marginTop: 10 }} />
              <div className="home-page-card" style={{ padding: 10 }}>
                <Row gutter={16}>
                  {myFav.map((res, index) => (
                    <Col span={8}>
                      <Card
                        style={{ marginBottom: 16 }}
                        cover={
                          <img
                            alt="favourite"
                            src={res.url}
                            style={{ height: 200 }}
                          />
                        }
                        actions={[<Icon type="delete" key="delete" />]}
                      >
                        <Skeleton loading={this.props.isLoading} avatar active>
                          <Meta
                            style={{ height: "10%" }}
                            title={res.id}
                            description={res.caption}
                          />
                        </Skeleton>
                      </Card>
                    </Col>
                  ))}
                </Row>
              </div>
            </div>
          </div>
        ) : null}
      </div>
    );
  }
}

export default Collect;
