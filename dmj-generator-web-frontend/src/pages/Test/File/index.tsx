import React, {useState} from 'react';
import {Flex} from "antd/lib";
import {Button, Card, Divider, message, Upload, UploadProps} from "antd";
import {InboxOutlined} from "@ant-design/icons";
import {testDownloadFileUsingGet, testUploadFileUsingPost} from "@/services/backend/fileController";
import {COS_HOST} from "@/constants";
import {saveAs} from "file-saver";

const {Dragger} = Upload;
const TestFilePage: React.FC = () => {
  const [value, setValue] = useState<string>()
  const props: UploadProps = {
    name: 'file',
    // multiple true 允许上传多个文件，false + maxCount: 1, 只能上传一个文件
    multiple: false,
    maxCount: 1,
    customRequest: async (fileObj: any) => {
      try {
        const res = await testUploadFileUsingPost({}, fileObj.file);
        fileObj.onSuccess(res.data)
        setValue(res.data)
      } catch (e: any) {
        message.error("上传失败。" + e.message)
        fileObj.onError(e)
      }

    },
    onRemove() {
      setValue(undefined);
    }
  };

  return (
    <Flex gap={16}>
      <Card title="文件上传">
        <Dragger {...props}>
          <p className="ant-upload-drag-icon">
            <InboxOutlined/>
          </p>
          <p className="ant-upload-text">Click or drag file to this area to upload</p>
          <p className="ant-upload-hint">
            Support for a single or bulk upload. Strictly prohibited from uploading company data or other
            banned files.
          </p>
        </Dragger>
      </Card>
      <Card title="文件下载">
        <div>
          文件地址：{COS_HOST + value}
        </div>
        <Divider/>
        <img src={COS_HOST + value} height={200}/>
        <Divider/>
        <Button onClick={async () => {
          const blob = await testDownloadFileUsingGet({filepath: value}, {responseType: 'blob'},);
          // 使用file-saver下载文件
          const fullPath = COS_HOST + value;
          saveAs(blob,fullPath.substring(fullPath.lastIndexOf("/") + 1))
        }}>点击下载文件</Button>
      </Card>
    </Flex>
  );
};
export default TestFilePage;
