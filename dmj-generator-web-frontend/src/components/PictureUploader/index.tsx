import React, {useState} from 'react';
import {message, Upload, UploadProps} from "antd";
import {uploadFileUsingPost} from "@/services/backend/fileController";
import {LockOutlined, PlusOutlined} from "@ant-design/icons";
import {COS_HOST} from "@/constants";

interface Props{
  biz:string;
  onChange?: (url:string)=>  void;
  value?: string;
}
const PictureUploader: React.FC<Props> = (props) => {
  const {biz,value,onChange}=props;
  const [loading,setLoading]  = useState<boolean>(false);


  const uploadProps: UploadProps = {
    name: 'file',
    // multiple true 允许上传多个文件，false + maxCount: 1, 只能上传一个文件
    multiple: false,
    listType: 'picture-card',
    maxCount: 1,
    showUploadList: false,
    disabled: loading,
    customRequest: async (fileObj: any) => {
      setLoading(true);
      try {
        const res = await uploadFileUsingPost({biz},{}, fileObj.file);
        const filePath = COS_HOST + res.data;
        onChange?.(filePath??'');
        fileObj.onSuccess(res.data)
      } catch (e: any) {
        message.error("上传失败。" + e.message)
        fileObj.onError(e)
      }
      setLoading(false);
    },
  };
  /**
   * 上传按钮
   */
  const uploadButton = (
    <button style={{border:0,background:'none'}} type='button'>
      {loading?<LockOutlined/>:<PlusOutlined/>}
      <div style={{marginTop:8}}></div>
    </button>
  )

  return (
    <Upload {...uploadProps}>
      {value ? <img src={value} alt='picture' style={{width:'100%'}}/> : uploadButton}
    </Upload>
  );
};
export default PictureUploader;
