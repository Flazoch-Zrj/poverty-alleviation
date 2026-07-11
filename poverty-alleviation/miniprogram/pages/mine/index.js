Page({
  data: { userInfo: {}, userInitial: '', pairInfo: null },
  onShow: function() {
    var ui = wx.getStorageSync('userInfo') || {}
    this.setData({ userInfo: ui, userInitial: (ui.realName || '?').charAt(0) })
  },
  navTo: function(e) { wx.navigateTo({ url: e.currentTarget.dataset.url }) },
  handleLogout: function() {
    wx.showModal({
      title: '退出登录', content: '确定退出当前账号？',
      success: function(r) { if (r.confirm) { wx.removeStorageSync('token'); wx.removeStorageSync('userInfo'); wx.reLaunch({ url: '/pages/login/index' }) } }
    })
  }
})
