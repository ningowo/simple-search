import React, { useState } from "react";
import { Avatar, List, Button, Spin, Modal, Input,notification } from "antd";
import { addFav, showFolder, addFolder } from "../util/APICollect";
import { Link, withRouter } from "react-router-dom";
function Text(props) {
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
        notification.error({
          message: "Search App",
          description:
            error.message ||
            "Sorry! Something went wrong. Please try again!",
        });
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
        notification.success({
          message: "Search App",
          description: "You're successfully add favourite.",
        });
      })
      .catch((error) => {
        notification.error({
          message: "Search App",
          description:
            error.message ||
            "Sorry! Something went wrong. Please try again!",
        });
      });
  };
  const onChangeInput = (event) => {
    const { value: inputValue } = event.target;
    console.log(inputValue);
    setFolderName(inputValue);
  };
  const clickAddFloder = (res) => {
    addFolder(props.userId,folderName)
      .then((response) => {
        addFav(response.data.id, docId)
        .then((response) => {
          notification.success({
            message: "Search App",
            description: "You're successfully add favourite.",
          });
        })
        .catch((error) => {
          notification.error({
            message: "Search App",
            description:
              error.message ||
              "Sorry! Something went wrong. Please try again!",
          });
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
        <List
          bordered
          itemLayout="horizontal"
          dataSource={props.data}
          renderItem={(res) => (
            <List.Item key={res.docId}>
              <Avatar src={res.url} alt="fav" />
              <a style={{ margin: 5 }} href={res.url}>
                {res.caption}
              </a>
              <div style={{ marginLeft: 15 }}>
                <Button
                  shape="circle"
                  icon="star"
                  onClick={() => {
                    showModal(res);
                  }}
                />
                <Modal
                  title="添加到我的收藏夹"
                  visible={isModalVisible}
                  onOk={handleOk}
                  onCancel={handleCancel}
                >
                  {!folder ? (
                    <Link to="/login">Login</Link>
                  ) : (
                    <div>
                      <div hidden={!folder.length == 0}>
                        <p>当前收藏夹为空，请先输入新建收藏夹的名称</p>
                        <Input.Group compact style={{ marginTop: 10 }}>
                          <Input
                            style={{ width: "calc(40%)" }}
                            onChange={onChangeInput}
                          />
                          <Button
                            icon="plus"
                            onClick={() => {
                              clickAddFloder(res);
                            }}
                          />
                        </Input.Group>
                      </div>

                      {folder.map((item, index) => (
                        <Button
                          icon="plus"
                          style={{ marginTop: 10 }}
                          onClick={() => loadFavFolder(item.id)}
                        >
                          {item.favouriteName}
                        </Button>
                      ))}
                    </div>
                  )}
                </Modal>
              </div>
            </List.Item>
          )}
        />
      )}
    </div>
  );
}
export default withRouter(Text);
