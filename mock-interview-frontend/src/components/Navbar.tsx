import '../styles/Navbar.css'
import { useState } from 'react';
export default function Navbar() {
    const [isMobileMenuOpen, setMobileMenuOpen] = useState(false);
    const toggleMobileMenu = () => {
    setMobileMenuOpen(!isMobileMenuOpen);
    };
  return (
    <nav className='navbar-container'>
        <div className='brand-name'>
            AI MOCK INTERVIEW
        </div>
        <div className="navbar-mobile-controls">
        <button 
            className="hamburger-icon" 
            onClick={toggleMobileMenu} 
            aria-label="Toggle navigation"
            aria-expanded={isMobileMenuOpen}
        >
            <span className="hamburger-line"></span>
            <span className="hamburger-line"></span>
            <span className="hamburger-line"></span>
        </button>
        </div>

        <div 
        className={isMobileMenuOpen ? "menu-backdrop menu-backdrop--open" : "menu-backdrop"}
        onClick={toggleMobileMenu} // Lets user click background to close
        />

        <ul className={`navigation-link-list navbar-container ${isMobileMenuOpen?'navbar-links-open':''}`}>
            <li>
                find questions
            </li>
            <li>
                admin
            </li>
            <li>
                contact
            </li>
            <li>
                about us
            </li>
        </ul>
    </nav>
  )
}
