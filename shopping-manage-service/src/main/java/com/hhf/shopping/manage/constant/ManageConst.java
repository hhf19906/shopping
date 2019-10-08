package com.hhf.shopping.manage.constant;

/**
 * Created by Administrator on 2019/10/8.
 */
public class ManageConst {
    public static final String SKUKEY_PREFIX="sku:";

    public static final String SKUKEY_SUFFIX=":info";

    public static final int SKUKEY_TIMEOUT=24*60*60;

    public static final int SKULOCK_EXPIRE_PX=10000; //锁的释放时间

    public static final String SKULOCK_SUFFIX=":lock"; //分布式锁
}
