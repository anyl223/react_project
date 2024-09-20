import { NextApiRequest, NextApiResponse } from 'next';

export default function handler(req: NextApiRequest, res: NextApiResponse) {
  if (req.method === 'POST') {
    res.writeHead(307, { Location: '/login' });
    res.end();
  } else {
    res.status(400).json({ error: 'Only POST requests are allowed.' });
  }
}