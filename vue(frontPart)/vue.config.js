module.exports = {
  publicPath: "/myNote",
  configureWebpack: {
    resolve: {
      alias: {
        'assets': '@/assets',
        'components': '@/components',
        'network': '@/network',
        'views': '@/views',
        'utils':'@/utils',
      }
    }
    
  }
    
}
