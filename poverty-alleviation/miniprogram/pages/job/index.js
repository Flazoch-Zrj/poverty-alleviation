var rq = require('../../utils/request')
Page({
  data: { list: [], keyword: '' },
  onShow: function() { this.loadData() },
  loadData: function() { var that=this; rq.get('/job/position/page',{page:1,size:20,keyword:that.data.keyword}).then(function(r){that.setData({list:r.data?.records||[]})}).catch(function(){}) },
  goDetail: function(e) { wx.navigateTo({ url: '/pages/job-detail/index?id=' + e.currentTarget.dataset.id }) }
})
