var rq = require('../../utils/request')
Page({
  data: { list: [], activeFilter: 0 },
  onShow: function() { this.loadData() },
  loadData: function() {
    var that = this
    rq.get('/news/article/page',{page:1,size:20}).then(function(r){
      var list = (r.data?.records || []).map(function(i) {
        i.createDate = i.createTime ? i.createTime.substring(0,10) : ''
        return i
      })
      that.setData({list: list})
    }).catch(function(){})
  },
  switchTab: function(e) { this.setData({ activeFilter: e.currentTarget.dataset.index }) },
  goDetail: function(e) { wx.navigateTo({ url: e.currentTarget.dataset.url }) }
})
