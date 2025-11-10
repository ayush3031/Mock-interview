import { Route, Routes } from 'react-router-dom'
import './App.css'
import { MantineProvider } from '@mantine/core'
import Home from './pages/Home'
import StartPage from './pages/StartPage'

function App() {
  return (
    <MantineProvider>
      <>
      <Routes>
        <Route path='/' element={<Home></Home>}/>
        <Route path='/start' element={<StartPage></StartPage>}/>
      </Routes>
      </>
    </MantineProvider>
  )
}

export default App
