var rq = require('../../utils/request')
Page({
  data: { list: [], activeFilter: 0 },
  onShow: function() { this.loadData() },
  loadData: function() {
    var that = this
    rq.get('/training/course/page',{page:1,size:20}).then(function(r){
      var list = (r.data?.records || []).map(function(i) {
        i.startDate = i.startTime ? i.startTime.substring(0,10) : '-'
        i.endDate = i.endTime ? i.endTime.substring(0,10) : '-'
        i.loadPct = i.maxEnroll > 0 ? Math.min(Math.round((i.enrolledCount||0)/i.maxEnroll*100), 100) : 0
        return i
      })
      that.setData({list: list})
    }).catch(function(){})
  },
  switchTab: function(e) { this.setData({ activeFilter: e.currentTarget.dataset.index }) },
  goDetail: function(e) { wx.navigateTo({ url: '/pages/training-detail/index?id=' + e.currentTarget.dataset.id }) }
})
