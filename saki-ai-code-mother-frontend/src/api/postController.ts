import myAxios from '@/request'

/**
 * 帖子游标分页查询请求
 */
export interface PostCursorQueryRequest {
  cursor?: string
  pageSize?: number
  categoryId?: number
  keyword?: string
  sortType?: string
}

/**
 * 游标分页响应
 */
export interface CursorPage<T> {
  records: T[]
  nextCursor: string
  hasMore: boolean
}

/**
 * 帖子视图对象
 */
export interface PostVO {
  id: string
  userId: string
  categoryId: string
  categoryName: string
  title: string
  firstImageUrl: string
  content: string
  viewCount: number
  likeCount: number
  commentCount: number
  isTop: number
  createTime: string
  user: UserVO
  isLiked: boolean
}

/**
 * 帖子详情视图对象
 */
export interface PostDetailVO {
  id: string
  userId: string
  categoryId: string
  categoryName: string
  title: string
  content: string
  firstImageUrl: string
  viewCount: number
  likeCount: number
  commentCount: number
  isTop: number
  createTime: string
  user: UserVO
  isLiked: boolean
}

/**
 * 用户视图对象
 */
export interface UserVO {
  id: string
  userName: string
  userAvatar: string
  userProfile: string
}

/**
 * 帖子创建请求
 */
export interface PostCreateRequest {
  title: string
  content: string
  categoryId: number
}

/**
 * 帖子点赞请求
 */
export interface PostLikeRequest {
  postId: string
}

/**
 * 帖子更新请求
 */
export interface PostUpdateRequest {
  id: string
  title: string
  content: string
  categoryId: string
}

/**
 * 帖子分页查询请求（管理员用）
 */
export interface PostQueryRequest {
  id?: string
  title?: string
  userId?: string
  categoryId?: string
  isTop?: number
  pageNum?: number
  pageSize?: number
}

/**
 * 帖子置顶请求
 */
export interface PostTopRequest {
  id: string
  isTop: number
}

/**
 * 分页响应
 */
export interface PageResponse<T> {
  records: T[]
  totalRow: number
  pageNumber: number
  pageSize: number
}

/**
 * 游标分页查询帖子列表
 * @param params 查询参数
 */
export async function getPostCursorPage(params: PostCursorQueryRequest) {
  const res = await myAxios.get('/post/cursor/list', { params })
  return res.data
}

/**
 * 创建帖子
 * @param data 创建请求参数
 */
export async function createPost(data: PostCreateRequest) {
  const res = await myAxios.post('/post/create', data)
  return res.data
}

/**
 * 获取帖子详情
 * @param id 帖子ID
 */
export async function getPostDetail(id: string) {
  const res = await myAxios.get(`/post/${id}`)
  return res.data
}

/**
 * 点赞帖子
 * @param data 点赞请求参数
 */
export async function likePost(data: PostLikeRequest) {
  const res = await myAxios.post('/post/like', data)
  return res.data
}

/**
 * 取消点赞
 * @param data 取消点赞请求参数
 */
export async function unlikePost(data: PostLikeRequest) {
  const res = await myAxios.post('/post/unlike', data)
  return res.data
}

/**
 * 更新帖子
 * @param data 更新请求参数
 */
export async function updatePost(data: PostUpdateRequest) {
  const res = await myAxios.put('/post/update', data)
  return res.data
}

/**
 * 删除帖子
 * @param id 帖子ID
 */
export async function deletePost(id: string) {
  const res = await myAxios.delete(`/post/${id}`)
  return res.data
}

/**
 * 分页查询帖子列表（管理员用）
 * @param params 查询参数
 */
export async function listPostByPage(params: PostQueryRequest) {
  const res = await myAxios.get('/post/admin/list', { params })
  return res.data
}

/**
 * 设置帖子置顶状态（管理员用）
 * @param data 置顶请求参数
 */
export async function setPostTop(data: PostTopRequest) {
  const res = await myAxios.put('/post/admin/top', data)
  return res.data
}
