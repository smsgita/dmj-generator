package com.dmj.web.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dmj.web.common.ErrorCode;
import com.dmj.web.constant.CommonConstant;
import com.dmj.web.exception.BusinessException;
import com.dmj.web.exception.ThrowUtils;
import com.dmj.web.model.dto.generator.GeneratorEsDTO;
import com.dmj.web.model.dto.generator.GeneratorQueryRequest;
import com.dmj.web.model.entity.*;
import com.dmj.web.model.vo.GeneratorVO;
import com.dmj.web.model.vo.UserVO;
import com.dmj.web.service.GeneratorService;
import com.dmj.web.mapper.GeneratorMapper;
import com.dmj.web.service.UserService;
import com.dmj.web.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author dmj
* @description 针对表【generator(代码生成器)】的数据库操作Service实现
* @createDate 2024-01-19 15:15:28
*/
@Slf4j
@Service
public class GeneratorServiceImpl extends ServiceImpl<GeneratorMapper, Generator>
    implements GeneratorService{

    @Resource
    private UserService userService;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public void validGenerator(Generator generator, boolean add) {
        if (generator == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = generator.getName();
        String description = generator.getDescription();
        String tags = generator.getTags();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name, description, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 256) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param generatorQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest) {
        QueryWrapper<Generator> queryWrapper = new QueryWrapper<>();
        if (generatorQueryRequest == null) {
            return queryWrapper;
        }
        Long id = generatorQueryRequest.getId();
        Long notId = generatorQueryRequest.getNotId();
        String searchText = generatorQueryRequest.getSearchText();
        List<String> tags = generatorQueryRequest.getTags();
        List<String> orTags = generatorQueryRequest.getOrTags();
        Long userId = generatorQueryRequest.getUserId();
        String name = generatorQueryRequest.getName();
        String description = generatorQueryRequest.getDescription();
        String basePackage = generatorQueryRequest.getBasePackage();
        String version = generatorQueryRequest.getVersion();
        String author = generatorQueryRequest.getAuthor();
        String distPath = generatorQueryRequest.getDistPath();
        Integer status = generatorQueryRequest.getStatus();
        String sortField = generatorQueryRequest.getSortField();
        String sortOrder = generatorQueryRequest.getSortOrder();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(StringUtils.isNotBlank(basePackage), "basePackage", basePackage);
        queryWrapper.eq(StringUtils.isNotBlank(version), "version", version);
        queryWrapper.eq(StringUtils.isNotBlank(author), "author", author);
        queryWrapper.eq(StringUtils.isNotBlank(distPath), "distPath", distPath);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<Generator> searchFromEs(GeneratorQueryRequest generatorQueryRequest) {
        Long id = generatorQueryRequest.getId();
        Long notId = generatorQueryRequest.getNotId();
        String searchText = generatorQueryRequest.getSearchText();
        List<String> tagList = generatorQueryRequest.getTags();
        List<String> orTagList = generatorQueryRequest.getOrTags();
        Long userId = generatorQueryRequest.getUserId();
        // es 起始页为 0
        long current = generatorQueryRequest.getCurrent() - 1;
        long pageSize = generatorQueryRequest.getPageSize();
        String sortField = generatorQueryRequest.getSortField();
        String sortOrder = generatorQueryRequest.getSortOrder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        if (id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
        }
        if (notId != null) {
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("id", notId));
        }
        if (userId != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("userId", userId));
        }
        // 必须包含所有标签
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("tags", tag));
            }
        }
        // 包含任何一个标签即可
        if (CollUtil.isNotEmpty(orTagList)) {
            BoolQueryBuilder orTagBoolQueryBuilder = QueryBuilders.boolQuery();
            for (String tag : orTagList) {
                orTagBoolQueryBuilder.should(QueryBuilders.termQuery("tags", tag));
            }
            orTagBoolQueryBuilder.minimumShouldMatch(1);
            boolQueryBuilder.filter(orTagBoolQueryBuilder);
        }
        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("description", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }

        // 排序
        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
        if (StringUtils.isNotBlank(sortField)) {
            sortBuilder = SortBuilders.fieldSort(sortField);
            sortBuilder.order(CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
        }
        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);
        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest).withSorts(sortBuilder).build();
        SearchHits<GeneratorEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, GeneratorEsDTO.class);
        Page<Generator> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        List<Generator> resourceList = new ArrayList<>();
        page.setRecords(resourceList);
        return page;
    }

    @Override
    public GeneratorVO getGeneratorVO(Generator generator, HttpServletRequest request) {
        GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
        long generatorId = generator.getId();
        // 1. 关联查询用户信息
        Long userId = generator.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        generatorVO.setUser(userVO);
        return generatorVO;
    }

    @Override
    public Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage, HttpServletRequest request) {
        List<Generator> generatorList = generatorPage.getRecords();
        Page<GeneratorVO> generatorVOPage = new Page<>(generatorPage.getCurrent(), generatorPage.getSize(), generatorPage.getTotal());
        if (CollUtil.isEmpty(generatorList)) {
            return generatorVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = generatorList.stream().map(Generator::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> generatorIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> generatorIdHasFavourMap = new HashMap<>();

        // 填充信息
        List<GeneratorVO> generatorVOList = generatorList.stream().map(generator -> {
            GeneratorVO generatorVO = GeneratorVO.objToVo(generator);
            Long userId = generator.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            generatorVO.setUser(userService.getUserVO(user));
            return generatorVO;
        }).collect(Collectors.toList());
        generatorVOPage.setRecords(generatorVOList);
        return generatorVOPage;
    }
}




