import { appConfig } from '../../config/app.config'

export type TranscribeOptions = {
  language?: string
  model_size?: string
  batch_size?: number
  compute_type?: 'float16' | 'int8'
  diarize?: boolean
  min_speakers?: number
  max_speakers?: number
}

export async function transcribeWithWhisperX(file: File, options: TranscribeOptions = {}) {
  const form = new FormData()
  form.append('file', file)
  Object.entries(options).forEach(([k, v]) => {
    if (v !== undefined && v !== null) form.append(k, String(v))
  })

  const resp = await fetch(`${appConfig.asr.whisperxUrl}/transcribe`, {
    method: 'POST',
    body: form,
  })

  if (!resp.ok) {
    const text = await resp.text()
    throw new Error(`WhisperX error ${resp.status}: ${text}`)
  }
  return resp.json()
}


