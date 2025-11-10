import '../styles/ImageSection.css'
import { useNavigate } from 'react-router-dom'


export default function ImageSection() {
  const navigate=useNavigate();

  const handleGetStartedButton = () =>{
    navigate('/start');
  }
  return (
    <div className="image-section">
        <div className='image-content'>
            <h1>Prepare for Success with AI Mock Interviews</h1>
            <p>Hone your skills, get instant feedback, and ace your next interview.</p>
            <button className="get-started-button" onClick={handleGetStartedButton}>Get Started</button>
        </div>
    </div>
  )
}
