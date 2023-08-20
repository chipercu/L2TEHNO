package l2open.gameserver.skills.funcs;

public enum FuncPTS
{
	diff,
	per
	{
		@Override
		public double calc(double val, double param)
		{
			return val * param / 100;
		}
	};

	public double calc(double val, double param)
	{
		return val;
	}
}
/**
void __fastcall modify_stat(__int64 a1, int a2, int a3, double a4)
{
  signed __int64 v4; // r9@1
  signed __int64 v5; // rdx@1

  v4 = 2i64 * a2; // проценты
  v5 = 2 * (a2 + 194i64); // статик
  if ( a3 )
  {
    if ( a3 == 1 )
      *(double *)(a1 + 8 * v5) = a4 + *(double *)(a1 + 8 * v5);
  }
  else
  {
    *(double *)(a1 + 8 * v4 + 3096) = (a4 + 100.0) / 100.0 * *(double *)(a1 + 8 * v4 + 3096);
  }
}
**/