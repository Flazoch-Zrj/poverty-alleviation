function getToken() { try { return wx.getStorageSync('token') || null } catch(e) { return null } }
function setToken(t) { wx.setStorageSync('token', t) }
function removeToken() { wx.removeStorageSync('token'); wx.removeStorageSync('userInfo') }
function getUserInfo() { try { return wx.getStorageSync('userInfo') || null } catch(e) { return null } }
function setUserInfo(i) { wx.setStorageSync('userInfo', i) }
function getFamilyId() { return wx.getStorageSync('familyId') || '' }
function setFamilyId(id) { wx.setStorageSync('familyId', id) }
module.exports = { getToken, setToken, removeToken, getUserInfo, setUserInfo, getFamilyId, setFamilyId }
