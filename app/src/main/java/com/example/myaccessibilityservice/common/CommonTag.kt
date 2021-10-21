package com.example.myaccessibilityservice.common

object CommonTag {
    const val ACTION =  "action"
    const val ACTION_START_ACCESSIBILITY_SETTING = "action_start_accessibility_setting"
    const val ACTION_FINISH_SELF = "action_finis_self"
    const val BUTTON = "android.widget.Button"
    const val EDITTEXT = "android.widget.EditText"
    const val TextView = "android.widget.TextView"
    const val ImageView = "android.widget.ImageView"

    //点开红包--展示开的页面
    const val LUCKY_MONEY_RECEIVE_UI =
        "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI"
    //领取红包之后的页面
    const val LUCKY_MONEY_DETAIL_UI =
        "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI"

    //查看手机联系人页面
    const val SEE_PHONE_CONTACTS_PAGE="com.tencent.mm.plugin.account.bind.ui.MobileFriendUI"
    //申请添加朋友页面
    const val APPLY_FRIENDS_PAGE="com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI"
    //个人详情页
    const val APPLY_FRIENDS_PASS_PAGE="com.tencent.mm.plugin.profile.ui.ContactInfoUI"
    //查看手机联系人中的listview
    const val SEE_PHONE_CONTACTS_LISTVIEW="com.tencent.mm:id/fgs"

    //朋友圈页面
    const val CIRCLE_PAGE="com.tencent.mm.plugin.sns.ui.SnsTimeLineUI"
    //发现页面/朋友圈的id
    const val CIRCLE_ITEM="com.tencent.mm:id/h8z"
    //朋友圈item
    const val CIRCLE_DETAIL_ITEM="com.tencent.mm:id/hyc"
    //朋友圈点赞一整行
    const val CIRCLE_ZAN_LINE="com.tencent.mm:id/a2f"

    //朋友圈点赞图标按钮
    const val CIRCLE_ZAN_ITEM="com.tencent.mm:id/kn"
    //朋友圈整个listviewid
    const val CIRCLE_LISTVIEW="com.tencent.mm:id/hzr"
    //朋友圈点赞的弹框
    const val CIRCLE_ZAN_POP="com.tencent.mm:id/ka"



    /**------------------页面跳转------------------------------------*/
    const val ADD_FRIEND="ADD_FRIEND"
    const val AUTO_REPLAY="AUTO_REPLAY"
    const val CIRCLE_CLICK="CIRCLE_CLICK"
    const val CIRCLE_CLICK2="CIRCLE_CLICK2"
    const val JUMP_CONTACTS="JUMP_CONTACTS"
    const val RED_ENVELOPES="RED_ENVELOPES"
    const val OTHER="OTHER"

}