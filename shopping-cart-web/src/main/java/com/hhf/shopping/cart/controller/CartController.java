package com.hhf.shopping.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hhf.shopping.bean.CartInfo;
import com.hhf.shopping.bean.SkuInfo;
import com.hhf.shopping.config.CookieUtil;
import com.hhf.shopping.config.LoginRequire;
import com.hhf.shopping.service.CartService;
import com.hhf.shopping.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@CrossOrigin
public class CartController {

    @Reference
    CartService cartService;

    @Reference
    ManageService manageService;

    @Autowired
    CartCookieHandler cartCookieHandler;

    @RequestMapping("addToCart")
   @LoginRequire(autoRedirect = false)  //添加购物车不需要登录
    public String addToCart(HttpServletRequest request, HttpServletResponse response){
        //获取商品的数量
        String skuNum = request.getParameter("skuNum");
        String skuId = request.getParameter("skuId");

        String userId = (String) request.getAttribute("userId");//获取页面的userid
        if (userId!=null){
            //登录成功就可以添加购物车
            cartService.addToCart(skuId,userId,Integer.parseInt(skuNum));
        }else {
            //没有登录添加购物车
            cartCookieHandler.addToCart(request,response,skuId,userId,Integer.parseInt(skuNum));
        }

        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        request.setAttribute("skuNum",skuNum);
        request.setAttribute("skuInfo",skuInfo);
        return "success";
    }

    //结算购物车列表展示
    @RequestMapping("cartList")
    @LoginRequire(autoRedirect = false)
    public String cartList(HttpServletRequest request, HttpServletResponse response){
        String userId = (String) request.getAttribute("userId");//获取页面的userid
        List<CartInfo> cartInfoList = null;
        if (userId!=null){
            //合并购物车
            List<CartInfo> cartListCK = cartCookieHandler.getCartList(request);
            if (cartListCK!=null &&cartListCK.size()>0){
                cartInfoList = cartService.mergeToCartList(cartListCK,userId);
                cartCookieHandler.deleteCartCookie(request,response); //删除未能登录的购物车

            }else {
                //登录状态下查询购物车
                cartInfoList = cartService.getCartList(userId);
            }

        }else {
            //未登录状态下添加购物车
           cartInfoList = cartCookieHandler.getCartList(request);
        }
        request.setAttribute("cartInfoList",cartInfoList);
        return "cartList";
    }

    //购物车勾选状态
    @RequestMapping("checkCart")
    @LoginRequire(autoRedirect = false)
    @ResponseBody
    public void checkCart(HttpServletRequest request, HttpServletResponse response){
        String isChecked = request.getParameter("isChecked");
        String skuId = request.getParameter("skuId");
        String userId = (String) request.getAttribute("userId");
        if (userId!=null){ //登录状态
            cartService.checkCart(skuId,isChecked,userId);
        }else { //没有登录
            cartCookieHandler.checkCart(request,response,skuId,isChecked);
        }
    }

    //去结算功能
    @RequestMapping("toTrade")
    @LoginRequire
    public String toTrade(HttpServletRequest request,HttpServletResponse response){
        // 合并勾选的商品 未登录+登录
        List<CartInfo> cartListCK = cartCookieHandler.getCartList(request);

        String userId = (String) request.getAttribute("userId");
        if (cartListCK!=null && cartListCK.size()>0){
            // 合并
            cartService.mergeToCartList(cartListCK,userId);
            // 删除未登录数据
            cartCookieHandler.deleteCartCookie(request,response);
        }


        return "redirect://localhost:8081/trade";
    }

}
