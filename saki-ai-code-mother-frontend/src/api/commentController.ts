import myAxios from '@/request'

/**
 * 评论创建请求
 */
export interface CommentCreateRequest {
  postId: string
  parentCommentId?: string
  replyUserId?: string
  content: string
}

/**
 * 评论点赞请求
 */
export interface CommentLikeRequest {
  commentId: string
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
 * 评论视图对象
 */
export interface CommentVO {
  id: string
  postId: string
  userId: string
  user: UserVO
  parentCommentId: string | null
  replyUserId: string | null
  replyUser: UserVO | null
  content: string
  likeCount: number
  isLiked: boolean
  replyCount: number | null
  createTime: string
}

/**
 * 创建评论
 * @param data 创建请求参数
 */
export async function createComment(data: CommentCreateRequest) {
  const res = await myAxios.post('/comment/create', data)
  return res.data
}

/**
 * 获取帖子的一级评论列表
 * @param postId 帖子ID
 * @param sortType 排序类型（latest/hot，默认latest）
 */
export async function getCommentsByPostId(postId: string, sortType?: string) {
  const res = await myAxios.get(`/comment/list/${postId}`, {
    params: { sortType }
  })
  return res.data
}

/**
 * 获取评论的二级回复列表
 * @param parentCommentId 父评论ID
 */
export async function getRepliesByCommentId(parentCommentId: string) {
  const res = await myAxios.get(`/comment/replies/${parentCommentId}`)
  return res.data
}

/**
 * 删除评论
 * @param id 评论ID
 */
export async function deleteComment(id: string) {
  const res = await myAxios.delete(`/comment/${id}`)
  return res.data
}
