module.exports = {
  root: true,
  env: {
    es6: true,
    node: true,
  },
  extends: [
    'eslint:recommended',
    'google',
  ],
  rules: {
    'quotes': ['error', 'single'],
    'linebreak-style': 0,
    'max-len': [2, 250, 4, {'ignoreUrls': true}],
  },
};
