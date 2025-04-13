# Paper Trading Simulator

This project is a paper trading simulator that allows users to simulate buying and selling shares in a market environment. It provides a market feed screen where users can view current market data and execute trades.

## Features

- Real-time market feed display
- Buy and sell actions for shares
- User-friendly interface for trading simulation

## Project Structure

```
paper-trading-simulator
├── public
│   ├── index.html          # Main HTML file
│   └── favicon.ico         # Favicon for the application
├── src
│   ├── components          # React components
│   │   ├── MarketFeedTable.tsx  # Table for displaying market feeds
│   │   └── TradeActions.tsx      # Component for trade actions
│   ├── pages               # Application pages
│   │   └── MarketFeedScreen.tsx  # Main screen for market feeds
│   ├── styles              # CSS styles
│   │   └── MarketFeedScreen.css  # Styles for the market feed screen
│   ├── utils               # Utility functions
│   │   └── api.ts         # API functions for fetching market data
│   ├── App.tsx            # Main application component
│   ├── index.tsx          # Entry point for the React application
│   └── types              # Type definitions
│       └── MarketFeed.ts  # TypeScript interface for market feed data
├── package.json            # npm configuration file
├── tsconfig.json           # TypeScript configuration file
└── README.md               # Project documentation
```

## Installation

1. Clone the repository:
   ```
   git clone <repository-url>
   ```
2. Navigate to the project directory:
   ```
   cd paper-trading-simulator
   ```
3. Install the dependencies:
   ```
   npm install
   ```

## Usage

To start the application, run:
```
npm start
```
This will launch the application in your default web browser.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.

## License

This project is licensed under the MIT License.