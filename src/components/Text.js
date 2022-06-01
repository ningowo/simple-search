import React from "react";
import { Avatar, Comment } from "antd";


function Text() {
  const data = [
    {
      content: 'Ant Design Title 1',
    },
    {
      content: 'Ant Design Title 2',
    },
    {
      content: 'Ant Design Title 3',
    },
    {
      content: 'Ant Design Title 4',
    },
  ];
  return (
    <div>
      {data.map((item) => (
        <div>
        <Avatar src="https://joeschmoe.io/api/v1/random" alt="Han Solo" />
        <p>
          We supply a series of design principles, practical patterns and high
          quality design resources (Sketch and Axure), to help people create
          their product prototypes beautifully and efficiently.
          {item.content}
        </p>
      </div>
      ))}
    </div>
  );
}
export default Text;
