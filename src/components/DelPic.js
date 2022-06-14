import React, { useState } from "react";
import { Col, Icon, Card, Spin, Button, Modal, Input } from "antd";
import { addFav, showFolder, addFolder } from "../util/APICollect";
import { Link, withRouter } from "react-router-dom";
const { Meta } = Card;
function DelPic(props) {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [folderName, setFolderName] = useState("null");
  const [folder, setFolder] = useState(null);
  const [docId, setDocId] = useState(1);
  const showModal = (res) => {
    console.log("showModel", res);
    setDocId(res.docId);
    setIsModalVisible(true);
    showFolder(props.userId)
      .then((response) => {
        console.log("showFolder,response", response);
        setFolder(response.data);
      })
      .catch((error) => {
        if (error.status === 404) {
          console.log("404");
        }
      });
  };

  const handleOk = () => {
    setIsModalVisible(false);
  };

  const handleCancel = () => {
    setIsModalVisible(false);
  };
  const loadFavFolder = (favId) => {
    console.log("loadFavFolder need to add fav", favId, docId);
    addFav(favId, docId)
      .then((response) => {
        console.log("addFav", response);
      })
      .catch((error) => {
        if (error.status === 404) {
          console.log("404");
        } else {
          console.log("success");
        }
      });
  };
  const onChangeInput = (event) => {
    const { value: inputValue } = event.target;
    console.log(inputValue);
    setFolderName(inputValue);
  };
  const clickAddFloder = () => {
    addFolder(folderName)
      .then((response) => {
        console.log(response);
        this.setState({
          data: response.data,
        });
      })
      .catch((error) => {
        if (error.status === 404) {
          console.log("404");
        } else {
          console.log("success");
        }
      });
  };
  return (
    <div>
      {!props.data ? (
        <Spin />
      ) : (
        props.data.map((res, index) => (
          <Col
            lg={{ span: 4, offset: 2 }}
            sm={{ span: 9, offset: 2 }}
            xs={{ span: 16, offset: 4 }}
            key={index}
          >
            <Card
              style={{ marginBottom: 16 }}
              cover={
                <img alt="favourite" src={res.url} style={{ height: 200 }} />
              }
              actions={[
                <Icon
                  type="star"
                  key="add favorite"
                  onClick={() => {
                    showModal(res);
                  }}
                />,
              ]}
            >
              <Meta style={{ height: 50 }} description={res.caption} />
            </Card>
          </Col>
        ))
      )}
    </div>
  );
}
export default withRouter(DelPic);
