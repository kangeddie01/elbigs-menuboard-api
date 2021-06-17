package com.elbigs.controller.cms;

import com.elbigs.dto.MenuCategoryDto;
import com.elbigs.dto.MenuCategoryReqDto;
import com.elbigs.dto.ResponsDto;
import com.elbigs.entity.MenuCategoryUserEntity;
import com.elbigs.service.MenuCategoryService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/cms")
public class MenuCategoryController {

    @Autowired
    private MenuCategoryService menuCategoryService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/users/{userPk}/total-menu-categories")
    public ResponsDto totalUserMenuCategory(@PathVariable(name = "userPk") long userPk) {

        ResponsDto res = new ResponsDto();
        List<MenuCategoryDto> list = menuCategoryService.selectUserMenuCategoryrList(userPk);
        res.put("menu_categories", list);
        return res;
    }

    /**
     * 메뉴카테고리 리스트 ( 상점 )
     * @param userPk
     * @return
     */
    @GetMapping("/users/{userPk}/menu-categories")
    public ResponsDto userMenuCategory(@PathVariable(name = "userPk") long userPk) {

        ResponsDto res = new ResponsDto();
        List<MenuCategoryDto> list = menuCategoryService.selectUserMenuCategoryrList(userPk);
        res.put("menu_categories", list);
        return res;
    }

    /**
     * 메뉴카테고리 리스트 ( 시설 )
     * @param userPk
     * @return
     */
    @GetMapping("/users/{userPk}/menu-categories2")
    public ResponsDto userMenuCategory2(@PathVariable(name = "userPk") long userPk) {

        ResponsDto res = new ResponsDto();
        List<MenuCategoryDto> list = menuCategoryService.selectUserMenuCategoryrList2(userPk);
        res.put("menu_categories", list);
        return res;
    }

    @PostMapping("/users/{userPk}/menu-categories/{categoryId}")
    public ResponsDto menuCategoryShow(@ModelAttribute MenuCategoryReqDto req
            , @PathVariable(name = "userPk") long userPk
            , @PathVariable(name = "categoryId") long categoryId) {

        req.setUserPk(userPk);
        req.setCategoryId(categoryId);

        menuCategoryService.updateMenuCategoryShow(req);
        return new ResponsDto();
    }

    @PostMapping("/users/{userPk}/menu-categories")
    public ResponsDto sortMenuCategory(HttpServletRequest req, @PathVariable(name = "userPk") long userPk) {

        Enumeration e = req.getParameterNames();

        MenuCategoryUserEntity entity = new MenuCategoryUserEntity(userPk);

        int order = 0;
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            String[] values = req.getParameterValues(name);

            if (name.contains("order") || !name.contains("category")) {
                continue;
            }

            for (String value : values) {

                entity.setMenuCategoryId(Long.parseLong(value));
                entity.setOrder(order++);

                menuCategoryService.updateMenuCategoryOrder(entity);
                entity = new MenuCategoryUserEntity(userPk);
            }
        }
        return new ResponsDto();
    }

}
