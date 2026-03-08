import myAxios from '@/request'

/**
 * 上传图片
 * @param file 图片文件
 */
export async function uploadImage(file: File): Promise<any> {
  const formData = new FormData()
  formData.append('file', file)
  const res = await myAxios.post('/file/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
  return res.data
}
