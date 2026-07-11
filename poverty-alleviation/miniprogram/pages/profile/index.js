var rq = require('../../utils/request')
Page({
  data: { family: {}, members: [], pairInfo: {}, incomeText: '' },
  onShow: function() {
    var that = this, fid = wx.getStorageSync('familyId') || ''
    if (!fid) return
    rq.get('/poverty/family/' + fid).then(function(r) {
      var d = r.data || {}
      var fam = d.family || d
      var inc = (fam && fam.annualIncome) ? fam.annualIncome.toLocaleString() : ''
      var members = (d.members || []).map(function(m) { m.nameInitial = m.name ? m.name.charAt(0) : '?'; return m })
      that.setData({ family: fam || {}, members: members, incomeText: inc })
    }).catch(function(){})
    rq.get('/assistance/pairing/page', { familyId: Number(fid), page: 1, size: 1 }).then(function(r) {
      var list = r.data?.records || r.data?.list || []
      if (list.length) that.setData({ pairInfo: list[0] || {} })
    }).catch(function(){})
  },
  goEdit: function() { wx.navigateTo({ url: '/pages/edit-family/index' }) },
  goMembers: function() { wx.navigateTo({ url: '/pages/profile-members/index' }) }
})
