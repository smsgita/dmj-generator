import {
  PageContainer
} from '@ant-design/pro-components';
import React, {useEffect, useState} from 'react';
import {Button, Card, Col, Image, message, Row, Space, Tabs, Tag, Typography} from 'antd';
import {downloadGeneratorByIdUsingGet, getGeneratorVoByIdUsingGet} from "@/services/backend/generatorController";
import moment from "moment";
import {DownlandOutline} from "antd-mobile-icons";
import FileConfig from "@/pages/Generator/Detail/components/FileConfig";
import ModelConfig from "@/pages/Generator/Detail/components/ModelConfig";
import AuthorInfo from "@/pages/Generator/Detail/components/AuthorInfo";
import {DownloadOutlined, EditOutlined} from "@ant-design/icons";
import {Link} from '@umijs/max';
import {saveAs} from "file-saver";
import {useModel, useParams} from "@@/exports";

/**
 * 生成器详情页
 * @constructor
 */
const GeneratorDetailPage: React.FC = () => {
  // const [searchParams] = useSearchParams(); 从 请求地址的？后面 获取数据
  const {id} = useParams(); // 从路由中取数据
  const [loading,setLoading] = useState<boolean>(true);
  const [data, setData] = useState<API.GeneratorVO>({});
  const { initialState, setInitialState } = useModel('@@initialState');
  const { currentUser } = initialState ?? {};
  const my = data?.userId === currentUser?.id;
  /**
   * 加载数据
   */
  const loadData = async () => {
    if (!id) {
      return;
    }
    setLoading(true);
    try {
      const res = await getGeneratorVoByIdUsingGet({id});
      // url转换为文件列表
      if (res.data) {
        setData(res.data??{});
      }
    } catch (e: any) {
      message.error("加载数据失败，",e.message);
    }
    setLoading(false);
  }

  useEffect(() => {
    if (!id) {
      return;
    }
    loadData();
  }, [id])

  /**
   * 标签列表
   * @param tags
   */
  const tagListView = (tags?: string[]) => {
    if (!tags) {
      return <></>
    }
    return <div style={{marginBottom: 8}}>
      {tags.map((tag) => (
        <Tag key={tag}>{tag}</Tag>
      ))}
    </div>
  }
  /**
   * 下载按钮
   */
  const downloadButton = data.distPath && currentUser && (
    <Button
      icon={<DownloadOutlined />}
      onClick={async () => {
        const blob = await downloadGeneratorByIdUsingGet(
          {
            id: data.id,
          },
          {
            responseType: 'blob',
          },
        );
        // 使用 file-saver 来保存文件
        const fullPath = data.distPath || '';
        saveAs(blob, fullPath.substring(fullPath.lastIndexOf('/') + 1));
      }}
    >
      下载
    </Button>
  );



  /**
   * 编辑按钮
   */
  const editButton = my && (
    <Link to={`/generator/update?id=${data.id}`}>
      <Button icon={<EditOutlined />}>编辑</Button>
    </Link>
  );
  return (
    <PageContainer title={<></>} loading={loading}>
      <Card>
        <Row justify={"space-between"} gutter={[32, 32]}>
          <Col flex={"auto"}>
            <Space size="large" align="center">
              <Typography.Title level={4}>{data.name}</Typography.Title>
              {tagListView(data.tags)}
            </Space>
            <Typography.Paragraph type="secondary">{data.description}</Typography.Paragraph>
            <Typography.Paragraph type="secondary">
              创建时间:{moment(data.createTime).format('YYYY-MM-DD hh:mm:ss')}
            </Typography.Paragraph>
            <Typography.Paragraph type="secondary">基础包:{data.basePackage}</Typography.Paragraph>
            <Typography.Paragraph type="secondary">版本:{data.version}</Typography.Paragraph>
            <Typography.Paragraph type="secondary">作者:{data.author}</Typography.Paragraph>
            <div style={{marginBottom: 24}}/>
            <Space size="middle">
              <Link to={`/generator/use/${data.id}`}>
                <Button type="primary">立即使用</Button>
              </Link>
              {downloadButton}
              {editButton}
            </Space>
          </Col>
          <Col flex='320px'>
            <Image src={data.picture}/>
          </Col>
        </Row>
      </Card>
      <div style={{marginBottom: 24}}/>
      <Card>
        <Tabs
        size="large"
        defaultActiveKey="fileConfig"
        onChange={()=>{}}
        items={[
          {
            key:'fileConfig',
            label: '配置文件',
            children: <FileConfig data={data}/>
          },
          {
            key:'modelConfig',
            label: '配置模型',
            children: <ModelConfig data={data}/>
          },
          {
            key:'userInfo',
            label: '用户信息',
            children: <AuthorInfo data={data}/>
          },
        ]}></Tabs>
      </Card>
    </PageContainer>
  );
};
export default GeneratorDetailPage;
