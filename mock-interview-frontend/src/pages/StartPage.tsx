// src/pages/StartPage/StartPage.tsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/StartPage.css';

// --- 1. Define the file upload states ---
type FileUploadStatus = 'idle' | 'loading' | 'success';

const StartPage: React.FC = () => {
  const [jobRole, setJobRole] = useState('');
  const [companyName, setCompanyName] = useState('');
  const [jobDescription, setJobDescription] = useState('');
  
  // --- 2. Rename states to be file-specific ---
  const [resumeFile, setResumeFile] = useState<File | null>(null);
  const [fileUploadStatus, setFileUploadStatus] = useState<FileUploadStatus>('idle');

  const navigate = useNavigate();

  // --- 3. Make the file handler async to simulate upload ---
  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setFileUploadStatus('loading');
      setResumeFile(e.target.files[0]);

      // --- Simulate an API call (e.g., uploading the file) ---
      await new Promise(resolve => setTimeout(resolve, 2000));
      // --- End of simulation ---

      setFileUploadStatus('success');
    } else {
      setResumeFile(null);
      setFileUploadStatus('idle');
    }
  };

  const handleStartInterview = (e: React.FormEvent) => {
    e.preventDefault(); 
    
    // Check if a file is still uploading
    if (fileUploadStatus === 'loading') {
      alert("Please wait for the file to finish uploading.");
      return;
    }
    
    console.log({
      role: jobRole,
      company: companyName,
      description: jobDescription,
      resume: resumeFile ? resumeFile.name : 'No resume'
    });
    
    navigate('/interview'); 
  };
  
  // --- 4. Helper function for the *file button* text ---
  const getFileButtonText = () => {
    if (fileUploadStatus === 'loading') return 'Uploading...';
    if (fileUploadStatus === 'success') return 'Uploaded!';
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
          {/* We hide the real input */}
          <input 
            type="file" 
            id="resume-upload"
            onChange={handleFileChange}
            accept=".pdf,.doc,.docx"
            disabled={fileUploadStatus === 'loading'}
          />
          
          {/* This is our new styled button */}
          <label 
            htmlFor="resume-upload" 
            className={`file-upload-button ${fileUploadStatus}`}
          >
            {fileUploadStatus === 'loading' && <span className="spinner"></span>}
            {getFileButtonText()}
          </label>
        </div>
        {/* ----------------------------------------------- */}

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