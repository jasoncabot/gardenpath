import React from 'react';
import './App.css';
import { greeter } from '@app/shared';

function App() {
  const message = greeter();
  return (
    <h1 className="text-3xl font-bold underline text-red-600">
      {message}
    </h1>
  );
}

export default App;
