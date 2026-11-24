// src/pages/StartPage/StartPage.tsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { isAxiosError } from 'axios';
import { api } from '../api/client';
import '../styles/StartPage.css';

// --- 1. Define the file upload states ---
type FileUploadStatus = 'idle' | 'loading' | 'success' | 'error';

// Shape of the backend response from POST /api/resumes
interface ResumeUploadResponse {
  resumeId: number;
  originalFilename: string;
  charCount: number;
  extractedText: string;
}

const StartPage: React.FC = () => {
  const [jobRole, setJobRole] = useState('');
  const [companyName, setCompanyName] = useState('');
  const [jobDescription, setJobDescription] = useState('');

  // --- 2. File-specific state ---
  const [resumeFile, setResumeFile] = useState<File | null>(null);
  const [fileUploadStatus, setFileUploadStatus] = useState<FileUploadStatus>('idle');
  const [resumeId, setResumeId] = useState<number | null>(null); // backend id
  const [uploadProgress, setUploadProgress] = useState(0);       // 0–100

  const navigate = useNavigate();

  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) {
      setResumeFile(null);
      setResumeId(null);
      setFileUploadStatus('idle');
      setUploadProgress(0);
      return;
    }

    setResumeFile(file);
    setFileUploadStatus('loading');
    setResumeId(null);
    setUploadProgress(0);

    const formData = new FormData();
    formData.append('file', file); // key MUST match @RequestParam("file")

    try {
      // ⚠️ Do NOT set Content-Type manually — axios/browser adds the multipart boundary
      const { data } = await api.post<ResumeUploadResponse>(
        '/api/resumes',
        formData,
        {
          onUploadProgress: (evt) => {
            if (evt.total) {
              setUploadProgress(Math.round((evt.loaded / evt.total) * 100));
            }
          },
        }
      );

      setResumeId(data.resumeId);
      setFileUploadStatus('success');
      setUploadProgress(100);
    } catch (err) {
      console.error('Resume upload failed:', err);

      // Surface the backend's message (e.g. "Only PDF, DOCX, or TXT allowed")
      if (isAxiosError(err)) {
        const backendMessage =
          (err.response?.data as { message?: string } | undefined)?.message;
        if (backendMessage) console.warn('Backend said:', backendMessage);
      }

      setFileUploadStatus('error');
      setResumeFile(null);
      setResumeId(null);
      setUploadProgress(0);
      // Reset the input so re-selecting the same file triggers onChange again
      e.target.value = '';
    }
  };

  const handleStartInterview = (e: React.FormEvent) => {
    e.preventDefault();

    if (fileUploadStatus === 'loading') {
      alert('Please wait for the file to finish uploading.');
      return;
    }
    if (fileUploadStatus === 'error') {
      alert('Resume upload failed. Please try again or remove the file.');
      return;
    }

    // ✅ Pass real data to the interview page via router state
    navigate('/interview', {
      state: {
        jobRole,
        companyName,
        jobDescription,
        resumeId, // null if no file uploaded
      },
    });
  };

  // --- 4. Helper function for the file button text ---
  const getFileButtonText = () => {
    if (fileUploadStatus === 'loading') return `Uploading... ${uploadProgress}%`;
    if (fileUploadStatus === 'success') return 'Uploaded!';
    if (fileUploadStatus === 'error') return 'Upload failed — try again';
    return 'Upload Resume (Optional)';
  };

  return (
    <div className="start-page-container">
      <form className="start-form-card" onSubmit={handleStartInterview}>

        <h2>Interview Setup</h2>
        <p>Tell us about the role you're practicing for.</p>

        <div className="form-group">
          <label htmlFor="job-role">Job Role*</label>
          <input
            type="text"
            id="job-role"
            placeholder="e.g., Software Engineer"
            value={jobRole}
            onChange={(e) => setJobRole(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="company-name">Company Name (Optional)</label>
          <input
            type="text"
            id="company-name"
            placeholder="e.g., Google, Amazon"
            value={companyName}
            onChange={(e) => setCompanyName(e.target.value)}
          />
        </div>

        <div className="form-group">
          {/* Hidden real input */}
          <input
            type="file"
            id="resume-upload"
            onChange={handleFileChange}
            accept=".pdf,.docx,.txt"
            disabled={fileUploadStatus === 'loading'}
          />

          {/* Styled button */}
          <label
            htmlFor="resume-upload"
            className={`file-upload-button ${fileUploadStatus}`}
          >
            {fileUploadStatus === 'loading' && <span className="spinner"></span>}
            {getFileButtonText()}
          </label>
        </div>

        <div className="form-group">
          <label htmlFor="job-desc">Job Description (Optional)</label>
          <textarea
            id="job-desc"
            placeholder="Paste the job description for tailored questions..."
            rows={6}
            value={jobDescription}
            onChange={(e) => setJobDescription(e.target.value)}
          />
        </div>

        <button type="submit" className="start-interview-button">
          Start Interview
        </button>

      </form>
    </div>
  );
};

export default StartPage;