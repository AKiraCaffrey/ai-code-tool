import myAxios from '@/request'

/**
 * 帖子分类视图对象
 */
export interface PostCategoryVO {
  id: string
  name: string
  sortOrder: number
}

/**
 * 获取帖子分类列表
 */
export async function listPostCategory() {
  const res = await myAxios.get('/postCategory/list')
  return res.data
}
