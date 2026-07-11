var rq = require('../../utils/request')
Page({
  data: { list: [], activeFilter: 0 },
  onShow: function() { this.loadData() },
  loadData: function() {
    var that = this
    rq.get('/need/page',{page:1,size:20}).then(function(r){
      var list = (r.data?.records || []).map(function(i) {
        i.fmtTarget = i.targetAmount ? i.targetAmount.toLocaleString() : '-'
        i.progressPct = i.targetAmount > 0 ? Math.min(Math.round(i.actualAmount / i.targetAmount * 100), 100) : 0
        return i
      })
      that.setData({list: list})
    }).catch(function(){})
  },
  switchTab: function(e) { this.setData({ activeFilter: e.currentTarget.dataset.index }) },
  goAdd: function() { wx.navigateTo({ url: '/pages/need-add/index' }) }
})
