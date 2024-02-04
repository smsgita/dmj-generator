import {
  PageContainer,
  ProFormSelect,
  ProFormText,
  QueryFilter
} from '@ant-design/pro-components';
import {Card, Input, message, List, Tag, Image, Typography, Avatar} from 'antd';
import React, {useEffect, useState} from 'react';
import {listGeneratorVoByPageUsingPost} from "@/services/backend/generatorController";
import {UserOutlined} from '@ant-design/icons/lib/icons';
import moment from "moment";
// import Flex from 'antd/lib/flex';
import {Tabs, Flex} from 'antd/lib';
import {Link} from "umi";

const DEFAULT_PAGE_PARAMS: PageRequest = {
  current: 1,
  pageSize: 4,
  sortField: 'createTime',
  sortOrder: 'descend'
}
const IndexPage: React.FC = () => {
  //
  const [loading, setLoding] = useState<boolean>(true);
  //
  const [dataList, setDataList] = useState<API.GeneratorVO[]>([])
  const [total, setTotal] = useState<number>(0)
  // 搜索条件
  const [searchParams, setSearchParams] = useState<API.GeneratorQueryRequest>({
    ...DEFAULT_PAGE_PARAMS
  })
  // 页面显示数据维护
  const doSearch = async () => {
    setLoding(true);
    try {
      const res = await listGeneratorVoByPageUsingPost(searchParams);
      setDataList(res.data?.records ?? []);
      setTotal(Number(res.data?.total) ?? 0);
    } catch (error: any) {
      message.error('获取数据失败' + error.message);
    }
    setLoding(false);
  }
  // 查询条件变更时重新请求和加载数据
  useEffect(() => {
    doSearch();
  }, [searchParams]);

  const tagListView = (tags?: string[]) => {
    if (!tags) {
      return <></>
    }
    return tags.map((tag) => (
      <Tag key={tag}>{tag}</Tag>
    ))

  }
  return (
    <PageContainer title={<></>}>
      <Flex>
        <Input.Search
          style={{
            width: '40vm',
            minWidth: 320,
          }}
          placeholder="请搜索生成器"
          allowClear
          enterButton="搜索"
          size="large"
          value={searchParams.searchText}
          onChange={(e) => {
            searchParams.searchText = e.target.value
          }}
          onSearch={(value: string) => {
            setSearchParams({
              ...DEFAULT_PAGE_PARAMS,
              searchText: value
            })
          }}
        />
      </Flex>
      <QueryFilter
        span={12}
        labelWidth="auto"
        labelAlign="left"
        style={{padding: '16px 0'}}
        onFinish={async (values: API.GeneratorQueryRequest) => {
          setSearchParams({
            ...DEFAULT_PAGE_PARAMS,
            searchText: searchParams.searchText,
            ...values
          })
        }}
      >
        <ProFormSelect label="标签" name='tags' mode='tags'/>
        <ProFormText label="名称" name="name"/>
        <ProFormText name="description" label="描述"/>
      </QueryFilter>
      <Tabs
        defaultActiveKey="newest"
        items={[
          {
            label: `最新`,
            key: 'newest',
            children:<div>
              <List<API.GeneratorVO>
                loading={loading}
                rowKey="id"
                grid={
                  {
                    gutter: 16,
                    xs: 1,
                    sm: 2,
                    md: 3,
                    lg: 3,
                    xl: 4,
                    xxl: 4,
                  }
                }
                dataSource={dataList}
                pagination={{
                  current: searchParams.current,
                  pageSize: searchParams.pageSize,
                  total,
                  onChange(current, pageSize) {
                    setSearchParams({
                      ...searchParams,
                      current,
                      pageSize,
                    })
                  }
                }}
                renderItem={(data) => (
                  <List.Item>
                    <Link to={`/generator/detail/${data.id}`}>
                      <Card hoverable cover={<Image alt={data.name} src={data.picture}/>}>
                        <Card.Meta
                          title={<a>{data.name}</a>}
                          description={
                            <Typography.Paragraph ellipsis={{rows: 2}}>
                              {data.description}
                            </Typography.Paragraph>
                          }/>
                        {tagListView(data.tags)}
                        <Flex justify="space-between">
                          <Typography.Paragraph type="secondary" style={{fontSize: 12}}>
                            {moment(data.createTime).fromNow()}
                          </Typography.Paragraph>
                          <div>
                            <Avatar size={"large"} src={data.user?.userAvatar ?? <UserOutlined/>}/>
                          </div>
                        </Flex>

                      </Card>
                    </Link>

                  </List.Item>
                )}
              />
            </div>
          },
          {
            label: `推荐`,
            key: 'recommend',
          },
        ]}
        onChange={() => {
        }}
      />
    </PageContainer>
  );
};
export default IndexPage;
