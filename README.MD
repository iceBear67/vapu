# Tools - Dumper
Dumper是一个用于dump class的实用工具包。  
Scanner负责收集.minecraft文件夹中已经存有的class作为黑名单  
Injector负责作为注入器注入MC监听transform class并匹配名单做到dump.  
  
该工具因为一些硬编码代码的原因，或许仅支持Windows。  
~~如果你能在linux下注入vape就做linux support~~  
请确保对`vapu_dump_out`文件夹开启了**大小写区分模式**  
例如： `fsutil file setCaseSensitiveInfo D:\Code\Vapu\build\libs\vapu_dump_out enable`  
通常 `vapu_dump_out` 会在Dumper同文件夹下
