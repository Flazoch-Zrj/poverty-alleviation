var rq = require('../../utils/request')
Page({
  data: { userInfo: {}, userInitial: '', pairInfo: null, tasks: [], newsList: [], skeleton: true },
  onShow: function() {
    var that = this
    var ui = wx.getStorageSync('userInfo') || {}
    that.setData({ userInfo: ui, userInitial: (ui.realName || '?').charAt(0) })
    that.loadNews()
    if (ui.userId) {
      rq.get('/assistance/pairing/page', { cadreUserId: ui.userId, page: 1, size: 1 }).then(function(res) {
        var list = res.data?.records || res.data?.list || []
        if (list.length > 0) { that.setData({ pairInfo: list[0] }); if (list[0].familyId) wx.setStorageSync('familyId', String(list[0].familyId)) }
      }).catch(function() {})
    }
  },
  loadNews: function() {
    var that = this
    rq.get('/news/article/page', { page: 1, size: 5 }).then(function(res) {
      var list = (res.data?.records || []).map(function(i) { i.createDate = i.createTime ? i.createTime.substring(0,10) : ''; return i })
      that.setData({ newsList: list, skeleton: false })
    }).catch(function() { that.setData({ skeleton: false }) })
  },
  navTo: function(e) { var url = e.currentTarget.dataset.url; if (url) wx.navigateTo({ url: url }) }
})
