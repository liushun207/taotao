package com.taotao.controller;

import com.taotao.common.annotations.Auth;
import com.taotao.common.pojo.*;
import com.taotao.common.utils.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品管理Controller
 * <p>Title: ItemController</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.com</p> 
 * @author
 * @date
 * @version 1.0
 */

@Controller
public class ItemController
{
    @Autowired
    private ItemService itemService;

    @RequestMapping("/item/{itemId}")
    @ResponseBody
    public TbItem getItemById(@PathVariable Long itemId)
    {
        TbItem tbItem = itemService.getItemById(itemId);
        return tbItem;
    }

    @RequestMapping("/item/list")
    @ResponseBody
    @Auth(modelNo = "item", actionNo = "list")
    public EUDataGridResult getItemList(Integer page, Integer rows)
    {
        EUDataGridResult result = itemService.getItemList(page, rows);
        return result;
    }

    @RequestMapping(value = "/item/save", method = RequestMethod.POST)
    @ResponseBody
    private TaotaoResult createItem(TbItem item, String desc, String itemParams) throws Exception
    {
        TaotaoResult result = itemService.createItem(item, desc, itemParams);
        return result;
    }

    @RequestMapping(value = "/item/vali", method = RequestMethod.POST)
    @ResponseBody
    // @RequestBody 标注为json格式
    public TaotaoResult validatableItem(@Valid @RequestBody ItemObject item, BindingResult bingingresult) throws Exception
    {
        if (bingingresult.hasErrors())
        {
            //如果没有通过,跳转提示
            Map<String, String> map = getErrors(bingingresult);
        }

        TaotaoResult result = new TaotaoResult();
        return result;
    }

    @RequestMapping(value = "/item/validatable", method = RequestMethod.POST)
    @ResponseBody
    public ResponseInstruction<Boolean> validatableItem(@RequestBody RequestInstruction<ItemObject> item) throws Exception
    {
        ResponseInstruction<Boolean> response = new ResponseInstruction<Boolean>();
        return response;
    }

    @RequestMapping(value = "/item/mapper", method = RequestMethod.POST)
    @ResponseBody
    public ResponseInstruction<List<OrganTo>> mapper() throws Exception
    {
        List<Organization> orgs = new ArrayList<>();

        Organization org1 = new Organization();
        org1.setCode("1");
        org1.setName("张三");

        Organization org2 = new Organization();
        org2.setCode("2");
        org2.setName("李四");

        orgs.add(org1);
        orgs.add(org2);

        List<OrganTo> result =  MapperUtils.mapTo(orgs, OrganTo.class);

        ResponseInstruction<List<OrganTo>> response = new ResponseInstruction<>(result);
        return response;
    }

    //region 私有

    /**
     * 获取错误
     *
     * @param result
     * @return
     */
    private Map<String, String> getErrors(BindingResult result)
    {
        Map<String, String> map = new HashMap<String, String>();
        List<FieldError> list = result.getFieldErrors();
        for (FieldError error : list)
        {
            System.out.println("error.getField():" + error.getField());
            System.out.println("error.getDefaultMessage():" + error.getDefaultMessage());

            map.put(error.getField(), error.getDefaultMessage());
        }
        return map;
    }

    //endregion
}


