var rq = require('../../utils/request')
Page({
  data: { job: {}, myApps: [], loading: false, jobId: 0 },
  onLoad: function(e) { if (e.id) this.setData({ jobId: Number(e.id) }); this.loadData() },
  loadData: function() {
    var that = this, jid = that.data.jobId
    if (!jid) return
    rq.get('/job/position/page',{page:1,size:50}).then(function(r){var list=r.data?.records||[],job=list.find(function(j){return j.jobId===jid});if(job)that.setData({job:job})}).catch(function(){})
    rq.get('/job/application/page',{jobId:jid}).then(function(r){var list=(r.data?.records||[]).map(function(i){i.applyDate=i.applyTime?i.applyTime.substring(0,10):'';return i});that.setData({myApps:list})}).catch(function(){})
  },
  handleApply: function() {
    var that=this; that.setData({loading:true})
    rq.post('/job/application',{jobId:that.data.jobId}).then(function(){wx.showToast({title:'申请成功',icon:'success'});that.loadData()}).catch(function(){}).finally(function(){that.setData({loading:false})})
  }
})
