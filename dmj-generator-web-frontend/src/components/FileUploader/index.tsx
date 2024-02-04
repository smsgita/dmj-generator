import React, {useState} from 'react';
import {message, Upload, UploadFile, UploadProps} from "antd";
import {InboxOutlined} from "@ant-design/icons";
import {uploadFileUsingPost} from "@/services/backend/fileController";

const {Dragger} = Upload;

interface Props{
  biz:string;
  onChange?: (fileList:UploadFile[])=>  void;
  value?: UploadFile[];
  description?: string;
}
const FileUploader: React.FC<Props> = (props) => {
  const {biz,value,description,onChange}=props;
  const [loading,setLoading]  = useState<boolean>(false);


  const uploadProps: UploadProps = {
    name: 'file',
    // multiple true 允许上传多个文件，false + maxCount: 1, 只能上传一个文件
    multiple: false,
    maxCount: 1,
    listType: 'text',
    fileList: value,
    disabled: loading,
    onChange({fileList}){
      onChange?.(fileList);
    },
    customRequest: async (fileObj: any) => {
      setLoading(true);
      try {
        const res = await uploadFileUsingPost({biz},{}, fileObj.file);
        fileObj.onSuccess(res.data)
      } catch (e: any) {
        message.error("上传失败。" + e.message)
        fileObj.onError(e)
      }
      setLoading(false);
    },
  };

  return (
    <Dragger {...uploadProps}>
      <p className="ant-upload-drag-icon">
        <InboxOutlined/>
      </p>
      <p className="ant-upload-text">点击或拖拽文件上传</p>
      <p className="ant-upload-hint">
        {description}
      </p>
    </Dragger>
  );
};
export default FileUploader;
