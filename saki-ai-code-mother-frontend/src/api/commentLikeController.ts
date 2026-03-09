import myAxios from '@/request'

/**
 * 评论点赞请求
 */
export interface CommentLikeRequest {
  commentId: string
}

/**
 * 点赞评论
 * @param data 点赞请求参数
 */
export async function likeComment(data: CommentLikeRequest) {
  const res = await myAxios.post('/commentLike/like', data)
  return res.data
}

/**
 * 取消点赞评论
 * @param data 取消点赞请求参数
 */
export async function unlikeComment(data: CommentLikeRequest) {
  const res = await myAxios.post('/commentLike/unlike', data)
  return res.data
}
